package com.mes.mes.service;

import com.mes.mes.entity.Defect;
import com.mes.mes.entity.DefectAction;
import com.mes.mes.entity.DefectType;
import com.mes.mes.entity.ProductionResult;
import com.mes.mes.entity.User;
import com.mes.mes.repository.DefectRepository;
import com.mes.mes.repository.ProductionResultRepository;
import com.mes.mes.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DefectService {

    private final DefectRepository defectRepository;
    private final ProductionResultRepository productionResultRepository;
    private final UserRepository userRepository;

    public DefectService(
            DefectRepository defectRepository,
            ProductionResultRepository productionResultRepository,
            UserRepository userRepository) {
        this.defectRepository = defectRepository;
        this.productionResultRepository = productionResultRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Defect> findAll() {
        return defectRepository.findAllForList();
    }

    @Transactional
    public void register(Long resultId, DefectType defectType, Integer defectQty, String note) {
        if (defectQty == null || defectQty <= 0) {
            throw new IllegalArgumentException("불량 수량은 1 이상이어야 합니다.");
        }
        ProductionResult result = productionResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("생산실적을 찾을 수 없습니다."));

        Defect d = new Defect();
        d.setProductionResult(result);
        d.setDefectType(defectType != null ? defectType : DefectType.UNKNOWN);
        d.setDefectQty(defectQty);
        d.setAction(DefectAction.HOLD);
        d.setNote(note);
        defectRepository.save(d);
    }

    @Transactional
    public void handle(Long defectId, DefectType defectType, DefectAction action, Long handledByUserId) {
        Defect d = defectRepository.findById(defectId)
                .orElseThrow(() -> new IllegalArgumentException("불량을 찾을 수 없습니다."));
        if (d.getHandledAt() != null) {
            throw new IllegalArgumentException("이미 처리된 불량입니다.");
        }
        User handler = userRepository.findById(handledByUserId)
                .orElseThrow(() -> new IllegalArgumentException("처리자를 찾을 수 없습니다."));
        if (defectType == null || action == null) {
            throw new IllegalArgumentException("불량 유형과 처리 방법을 선택하세요.");
        }
        if (action == DefectAction.HOLD) {
            throw new IllegalArgumentException("처리 시에는 REWORK 또는 SCRAP을 선택하세요.");
        }

        d.setDefectType(defectType);
        d.setAction(action);
        d.setHandledBy(handler);
        d.setHandledAt(LocalDateTime.now());
        defectRepository.save(d);
    }
}
