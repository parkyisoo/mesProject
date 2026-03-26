package com.mes.mes.service;

import com.mes.mes.entity.Lot;
import com.mes.mes.entity.LotHistory;
import com.mes.mes.entity.LotStatus;
import com.mes.mes.entity.User;
import com.mes.mes.repository.LotHistoryRepository;
import com.mes.mes.repository.LotRepository;
import com.mes.mes.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LotService {

    private final LotRepository lotRepository;
    private final LotHistoryRepository lotHistoryRepository;
    private final UserRepository userRepository;

    public LotService(
            LotRepository lotRepository,
            LotHistoryRepository lotHistoryRepository,
            UserRepository userRepository) {
        this.lotRepository = lotRepository;
        this.lotHistoryRepository = lotHistoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Lot> findAll() {
        return lotRepository.findAllWithDetails();
    }

    /**
     * 허용 전환만 성공. 실패 시 에러 메시지, 성공 시 null.
     */
    @Transactional
    public String changeStatus(Long lotId, LotStatus toStatus, Long changedByUserId) {
        Lot lot = lotRepository.findById(lotId).orElse(null);
        if (lot == null) {
            return "LOT을 찾을 수 없습니다.";
        }
        User changedBy = userRepository.findById(changedByUserId).orElse(null);
        if (changedBy == null) {
            return "사용자를 찾을 수 없습니다.";
        }

        LotStatus from = lot.getStatus();
        if (!isAllowedTransition(from, toStatus)) {
            return "허용되지 않는 상태 전환입니다: " + from + " → " + toStatus;
        }

        lot.setStatus(toStatus);
        lotRepository.save(lot);

        LotHistory history = new LotHistory();
        history.setLot(lot);
        history.setFromStatus(from.name());
        history.setToStatus(toStatus.name());
        history.setChangedBy(changedBy);
        history.setChangedAt(LocalDateTime.now());
        lotHistoryRepository.save(history);

        return null;
    }

    private static boolean isAllowedTransition(LotStatus from, LotStatus to) {
        if (from == LotStatus.WAITING && to == LotStatus.IN_PROGRESS) {
            return true;
        }
        if (from == LotStatus.IN_PROGRESS && to == LotStatus.COMPLETED) {
            return true;
        }
        if (from == LotStatus.IN_PROGRESS && to == LotStatus.ON_HOLD) {
            return true;
        }
        return false;
    }
}
