package com.mes.mes.service;

import com.mes.mes.entity.Defect;
import com.mes.mes.entity.DefectAction;
import com.mes.mes.entity.DefectType;
import com.mes.mes.entity.Lot;
import com.mes.mes.entity.LotStatus;
import com.mes.mes.entity.ProductionResult;
import com.mes.mes.entity.User;
import com.mes.mes.repository.DefectRepository;
import com.mes.mes.repository.LotRepository;
import com.mes.mes.repository.ProductionResultRepository;
import com.mes.mes.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ProductionResultService {

    private final LotRepository lotRepository;
    private final ProductionResultRepository productionResultRepository;
    private final DefectRepository defectRepository;
    private final UserRepository userRepository;
    private final LotService lotService;

    public ProductionResultService(
            LotRepository lotRepository,
            ProductionResultRepository productionResultRepository,
            DefectRepository defectRepository,
            UserRepository userRepository,
            LotService lotService) {
        this.lotRepository = lotRepository;
        this.productionResultRepository = productionResultRepository;
        this.defectRepository = defectRepository;
        this.userRepository = userRepository;
        this.lotService = lotService;
    }

    @Transactional(readOnly = true)
    public Lot getLotForResultForm(Long lotId) {
        return lotRepository.findByIdWithWorkOrderAndProduct(lotId).orElse(null);
    }

    /**
     * 실적 저장, 불량 자동 등록, LOT 완료 전환 및 이력.
     *
     * @throws IllegalArgumentException 비즈니스 규칙 위반 시 메시지 포함
     */
    @Transactional
    public void saveResult(
            Long lotId,
            Long operatorId,
            Integer goodQty,
            Integer defectQty,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        if (goodQty == null || defectQty == null) {
            throw new IllegalArgumentException("양품·불량 수량을 입력하세요.");
        }
        if (goodQty < 0 || defectQty < 0) {
            throw new IllegalArgumentException("수량은 0 이상이어야 합니다.");
        }

        Lot lot = lotRepository.findByIdWithWorkOrderAndProduct(lotId)
                .orElseThrow(() -> new IllegalArgumentException("LOT을 찾을 수 없습니다."));
        if (lot.getStatus() != LotStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("실적 입력은 진행 중(IN_PROGRESS) LOT만 가능합니다.");
        }
        if (productionResultRepository.existsByLot_LotId(lotId)) {
            throw new IllegalArgumentException("이미 실적이 등록된 LOT입니다.");
        }

        int planned = lot.getWorkOrder().getPlannedQty();
        if (goodQty + defectQty > planned) {
            throw new IllegalArgumentException("양품 수량과 불량 수량의 합이 계획수량을 초과할 수 없습니다.");
        }

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new IllegalArgumentException("작업자를 찾을 수 없습니다."));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime effectiveStart = startTime != null ? startTime : now;
        LocalDateTime effectiveEnd = endTime != null ? endTime : now;

        ProductionResult result = new ProductionResult();
        result.setLot(lot);
        result.setOperator(operator);
        result.setGoodQty(goodQty);
        result.setDefectQty(defectQty);
        result.setStartTime(effectiveStart);
        result.setEndTime(effectiveEnd);
        result.setInputAt(now);
        productionResultRepository.save(result);

        if (defectQty > 0) {
            Defect auto = new Defect();
            auto.setProductionResult(result);
            auto.setDefectType(DefectType.UNKNOWN);
            auto.setDefectQty(defectQty);
            auto.setAction(DefectAction.HOLD);
            auto.setHandledBy(null);
            auto.setHandledAt(null);
            auto.setNote("실적 입력 자동 등록");
            defectRepository.save(auto);
        }

        String lotErr = lotService.changeStatus(lotId, LotStatus.COMPLETED, operatorId);
        if (lotErr != null) {
            throw new IllegalStateException("LOT 상태 전환 실패: " + lotErr);
        }
    }
}
