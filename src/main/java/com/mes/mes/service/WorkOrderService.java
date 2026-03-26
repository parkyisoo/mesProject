package com.mes.mes.service;

import com.mes.mes.entity.Lot;
import com.mes.mes.entity.LotStatus;
import com.mes.mes.entity.MesProcess;
import com.mes.mes.entity.Product;
import com.mes.mes.entity.User;
import com.mes.mes.entity.WorkOrder;
import com.mes.mes.entity.WorkOrderStatus;
import com.mes.mes.repository.LotRepository;
import com.mes.mes.repository.MesProcessRepository;
import com.mes.mes.repository.ProductRepository;
import com.mes.mes.repository.UserRepository;
import com.mes.mes.repository.WorkOrderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WorkOrderService {

    private static final DateTimeFormatter DAY = DateTimeFormatter.BASIC_ISO_DATE;

    private final WorkOrderRepository workOrderRepository;
    private final LotRepository lotRepository;
    private final ProductRepository productRepository;
    private final MesProcessRepository mesProcessRepository;
    private final UserRepository userRepository;

    public WorkOrderService(
            WorkOrderRepository workOrderRepository,
            LotRepository lotRepository,
            ProductRepository productRepository,
            MesProcessRepository mesProcessRepository,
            UserRepository userRepository) {
        this.workOrderRepository = workOrderRepository;
        this.lotRepository = lotRepository;
        this.productRepository = productRepository;
        this.mesProcessRepository = mesProcessRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<WorkOrder> findAll() {
        return workOrderRepository.findAllWithProductAndProcess();
    }

    @Transactional(readOnly = true)
    public List<Product> listProductsForSelect() {
        return productRepository.findAll(Sort.by(Sort.Direction.ASC, "productName"));
    }

    @Transactional(readOnly = true)
    public List<MesProcess> listProcessesForSelect() {
        return mesProcessRepository.findAll(Sort.by(Sort.Direction.ASC, "processOrder"));
    }

    @Transactional
    public void create(Long productId, Long processId, Integer plannedQty, LocalDate plannedDate, Long createdByUserId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("제품을 찾을 수 없습니다."));
        MesProcess process = mesProcessRepository.findById(processId)
                .orElseThrow(() -> new IllegalArgumentException("공정을 찾을 수 없습니다."));
        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (plannedQty == null || plannedQty <= 0) {
            throw new IllegalArgumentException("수량은 1 이상이어야 합니다.");
        }
        if (plannedDate == null) {
            throw new IllegalArgumentException("예정일을 입력하세요.");
        }

        LocalDateTime now = LocalDateTime.now();
        String woNumber = nextWoNumber();
        String lotNumber = nextLotNumber();

        WorkOrder wo = new WorkOrder();
        wo.setWoNumber(woNumber);
        wo.setProduct(product);
        wo.setProcess(process);
        wo.setPlannedQty(plannedQty);
        wo.setStatus(WorkOrderStatus.OPEN);
        wo.setPlannedDate(plannedDate);
        wo.setCreatedBy(createdBy);
        wo.setCreatedAt(now);
        workOrderRepository.save(wo);

        Lot lot = new Lot();
        lot.setLotNumber(lotNumber);
        lot.setWorkOrder(wo);
        lot.setStatus(LotStatus.WAITING);
        lot.setCreatedAt(now);
        lotRepository.save(lot);
    }

    private String nextWoNumber() {
        String prefix = "WO-" + LocalDate.now().format(DAY) + "-";
        int seq = workOrderRepository.findFirstByWoNumberStartingWithOrderByWoNumberDesc(prefix)
                .map(WorkOrder::getWoNumber)
                .map(n -> nextSequenceAfter(prefix, n))
                .orElse(1);
        return prefix + String.format("%03d", seq);
    }

    private String nextLotNumber() {
        String prefix = "LOT-" + LocalDate.now().format(DAY) + "-";
        int seq = lotRepository.findFirstByLotNumberStartingWithOrderByLotNumberDesc(prefix)
                .map(Lot::getLotNumber)
                .map(n -> nextSequenceAfter(prefix, n))
                .orElse(1);
        return prefix + String.format("%03d", seq);
    }

    private static int nextSequenceAfter(String prefix, String currentFull) {
        if (currentFull == null || !currentFull.startsWith(prefix)) {
            return 1;
        }
        String suffix = currentFull.substring(prefix.length());
        try {
            return Integer.parseInt(suffix) + 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
