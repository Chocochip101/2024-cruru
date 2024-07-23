package com.cruru.process.service;

import com.cruru.applicant.controller.dto.DashboardApplicantResponse;
import com.cruru.applicant.domain.Applicant;
import com.cruru.applicant.domain.repository.ApplicantRepository;
import com.cruru.dashboard.domain.Dashboard;
import com.cruru.dashboard.domain.repository.DashboardRepository;
import com.cruru.dashboard.exception.DashboardNotFoundException;
import com.cruru.evaluation.domain.repository.EvaluationRepository;
import com.cruru.process.controller.dto.ProcessCreateRequest;
import com.cruru.process.controller.dto.ProcessResponse;
import com.cruru.process.controller.dto.ProcessesResponse;
import com.cruru.process.domain.Process;
import com.cruru.process.domain.repository.ProcessRepository;
import com.cruru.process.exception.ProcessBadRequestException;
import com.cruru.process.exception.ProcessNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProcessService {

    private static final int MAX_PROCESS_COUNT = 5;
    private static final int PROCESS_FIRST_SEQUENCE = 0;

    private final ApplicantRepository applicantRepository;
    private final ProcessRepository processRepository;
    private final DashboardRepository dashboardRepository;
    private final EvaluationRepository evaluationRepository;

    public ProcessesResponse findByDashboardId(long dashboardId) {
        boolean dashboardExists = dashboardRepository.existsById(dashboardId);
        if (!dashboardExists) {
            throw new DashboardNotFoundException();
        }

        return new ProcessesResponse(processRepository.findAllByDashboardId(dashboardId)
                .stream()
                .map(this::toProcessResponse)
                .toList());
    }

    private ProcessResponse toProcessResponse(Process process) {
        List<DashboardApplicantResponse> dashboardApplicantResponses = createDashboardApplicantResponses(process);

        return new ProcessResponse(
                process.getId(),
                process.getSequence(),
                process.getName(),
                process.getDescription(),
                dashboardApplicantResponses
        );
    }

    private List<DashboardApplicantResponse> createDashboardApplicantResponses(Process process) {
        return applicantRepository.findAllByProcess(process)
                .stream()
                .map(applicant -> toDashboardApplicantResponse(applicant, process))
                .toList();
    }

    private DashboardApplicantResponse toDashboardApplicantResponse(Applicant applicant, Process process) {
        int evaluationCount = evaluationRepository.countByApplicantAndProcess(applicant, process);
        return new DashboardApplicantResponse(
                applicant.getId(),
                applicant.getName(),
                applicant.getCreatedDate(),
                applicant.getIsRejected(),
                evaluationCount
        );
    }

    @Transactional
    public void create(long dashboardId, ProcessCreateRequest request) {
        List<Process> allByDashboardId = processRepository.findAllByDashboardId(dashboardId);
        validateProcessCount(allByDashboardId);
        Dashboard dashboard = dashboardRepository.findById(dashboardId)
                .orElseThrow(DashboardNotFoundException::new);

        allByDashboardId.stream()
                .filter(process -> process.getSequence() >= request.sequence())
                .forEach(Process::increaseSequenceNumber);

        processRepository.save(new Process(
                request.sequence(),
                request.name(),
                request.description(),
                dashboard)
        );
    }

    private void validateProcessCount(List<Process> processes) {
        if (processes.size() == MAX_PROCESS_COUNT) {
            throw new ProcessBadRequestException(
                    String.format("프로세스 수가 최대 %d개이므로 더 이상 생성할 수 없습니다.", MAX_PROCESS_COUNT)
            );
        }
    }

    @Transactional
    public void delete(long processId) {
        Process process = processRepository.findById(processId)
                .orElseThrow(ProcessNotFoundException::new);
        validateFirstOrLastProcess(process);
        validateExistsApplicant(process);
        processRepository.deleteById(processId);
    }

    private void validateFirstOrLastProcess(Process process) {
        int processCount = (int) processRepository.countByDashboard(process.getDashboard());
        if (process.isSameSequence(PROCESS_FIRST_SEQUENCE) || process.isSameSequence(processCount - 1)) {
            throw new ProcessBadRequestException("처음과 마지막 프로세스는 삭제할 수 없습니다.");
        }
    }

    private void validateExistsApplicant(Process process) {
        int applicantCount = (int) applicantRepository.countByProcess(process);
        if (applicantCount > 0) {
            throw new ProcessBadRequestException("지원자가 존재하는 프로세스는 삭제할 수 없습니다.");
        }
    }
}