package com.tibafit.service.workoutplan;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tibafit.dto.workoutplan.WorkoutPlanRequestDTO;
import com.tibafit.dto.workoutplan.WorkoutPlanResponseDTO;
import com.tibafit.model.workoutplan.WorkoutPlanConverter;
import com.tibafit.model.workoutplan.WorkoutPlanNotifyStatus;
import com.tibafit.model.workoutplan.WorkoutPlanSportFrom;
import com.tibafit.model.workoutplan.WorkoutPlanVO;
import com.tibafit.model.workoutplanrecord.WorkoutPlanRecordVO;
import com.tibafit.repository.workoutplan.WorkoutPlanRepository;
import com.tibafit.repository.workoutplanrecord.WorkoutPlanRecordRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@Transactional
public class workoutPlanService implements workoutPlanService_interface {

	@Autowired
	private WorkoutPlanRepository planRepo;
	
	@Autowired
	private WorkoutPlanRecordRepository planRecordRepo;
	
    @Autowired
    private WorkoutPlanRepository workoutPlanRepo;
    
    // 寄信人為 rrooMailSender
    @Autowired
    @Qualifier("rrooMailSender")
    private JavaMailSender rrooMailSender;
    
    @Value("${r.mail.username}")
    private String sendFromEmail;
    
    @Value("${r.mail.default}")
    private String defaultSendToEmail;


	@Override
	public void insertWorkoutPlanMultiple(List<WorkoutPlanRequestDTO> dtos) {
		List<WorkoutPlanVO> vos = WorkoutPlanConverter.toNewVoList(dtos);
		planRepo.saveAll(vos);
	}


	@Override
	public void updateWorkoutPlanMultiple(List<WorkoutPlanRequestDTO> dtos) {
	    List<Integer> ids = new ArrayList<>();
	    for (WorkoutPlanRequestDTO dto : dtos) {
	        if (dto != null && dto.getWorkoutPlanId() != null) {
	            ids.add(dto.getWorkoutPlanId());
	        }
	    }

	    // PO
	    List<WorkoutPlanVO> oriVos = planRepo.findAllById(ids);
	    
		List<WorkoutPlanVO> vos = WorkoutPlanConverter.toUpdateVoList(oriVos, dtos);
		
		planRepo.saveAll(vos);
	}


	@Override
	public WorkoutPlanResponseDTO getWorkoutPlanByPrimaryKey(Integer id) {
		WorkoutPlanVO vo = planRepo.findByWorkoutPlanId(id).orElse(null);
		WorkoutPlanResponseDTO dto = WorkoutPlanConverter.toDTO(vo);
		return dto;
	}

	@Override
	public List<WorkoutPlanResponseDTO> getWorkoutPlanByDateRange(Integer userId, LocalDate startDate, LocalDate endDate, List<Integer> statuses) {
		List<WorkoutPlanVO> vos = planRepo.findByUserIdAndWorkoutPlanDateBetweenAndWorkoutPlanDataStatusIn(userId, startDate, endDate, statuses);
		List<WorkoutPlanResponseDTO> dtos = WorkoutPlanConverter.toDtoList(vos);
		return dtos;
	}
	
	@Override
	public List<WorkoutPlanResponseDTO> getWorkoutPlanByDate(Integer userId, LocalDate workoutPlanDate, List<Integer> statuses) {
		List<WorkoutPlanVO> vos = planRepo.findByUserIdAndWorkoutPlanDateAndWorkoutPlanDataStatusIn(userId, workoutPlanDate, statuses);
		List<WorkoutPlanResponseDTO> dtos = WorkoutPlanConverter.toDtoList(vos);
		return dtos;
	}

	@Override
	public Integer updateWorkoutPlanDataStatusByIds(Integer status, List<Integer> ids) {
		if (ids == null || ids.isEmpty()) {
			return 0;
		}
		Integer affectNumOfPlan = planRepo.updateWorkoutPlanDataStatusByIds(status, ids);
		
		// 一併更新該計畫的紀錄資料狀態
		List<Integer> recordIds = new ArrayList<>();
		if(affectNumOfPlan > 0) {
			// PO
			List<WorkoutPlanRecordVO> recordVOs = planRecordRepo.findByWorkoutPlanIdIn(ids);
			
			for (WorkoutPlanRecordVO recordVO : recordVOs) {
				if(recordVO != null && recordVO.getWorkoutPlanRecordId() != null) {
					recordIds.add(recordVO.getWorkoutPlanRecordId());
				}
			}
		}
		
		planRecordRepo.updateWorkoutPlanRecordDataStatusByRecordIds(status, recordIds);
		
		return affectNumOfPlan;
	}
	
	
	
	// 每分鐘檢查一次
	@Scheduled(cron = "0 * * * * *")
	public void processReminders() {
		// 精確到分鐘
	    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Taipei"))
	                                     .truncatedTo(ChronoUnit.MINUTES);

		// 正常/補寄需提醒資料：開提醒/有日期/有時間/未刪除/未寄
	    // PO
	    List<WorkoutPlanVO> pendingPlans = workoutPlanRepo.findAllPendingNotifyPlans();

	    for (WorkoutPlanVO planVo : pendingPlans) {

	        Integer planId = planVo.getWorkoutPlanId();
	        LocalDate date = planVo.getWorkoutPlanDate();
	        LocalTime time = planVo.getWorkoutPlanTime();
	        if (planId == null || date == null || time == null) {
	        	continue;
	        };

	        // 計畫完整日期時間
	        LocalDateTime planDT = LocalDateTime.of(date, time);
	        // 應該提醒的時間 = 計畫時間 - 1 小時
	        LocalDateTime shouldNotifyDT = planDT.minusHours(1);

	        // now <-> 計畫開始 (分鐘數)
	        long minutesUntilPlan = Duration.between(now, planDT).toMinutes();

	        // 可正常提醒
	        // 計畫時間 約等於 現在+1小時（大於58分/小於60分，避早寄，容錯: 晚2分鐘)
	        if (minutesUntilPlan >= 58 && minutesUntilPlan <= 60) {
	        	try{
	        		sendEmail(planVo, WorkoutPlanNotifyStatus.SENT.getCodeNum());
	        		workoutPlanRepo.updateWorkoutPlanNotifyStatusById(
	        				planId, WorkoutPlanNotifyStatus.SENT.getCodeNum()
	        		);      		
	        	} catch(Exception err) {
	                // 不更狀態，讓下批再試一次
	                continue;
	        	}
	            // 避補寄
	            continue;
	        }

	        // 需補寄提醒
	        // 應該提醒的時間已過，但仍在 開始前 57 分鐘內 - 開始後 120 分鐘內
	        if (minutesUntilPlan >= -120 && minutesUntilPlan < 58) {
	            try {
		            sendEmail(planVo, WorkoutPlanNotifyStatus.MAKEUP.getCodeNum());
		            workoutPlanRepo.updateWorkoutPlanNotifyStatusById(
		                    planId, WorkoutPlanNotifyStatus.MAKEUP.getCodeNum()
		            );
	            } catch(Exception err) {
	                // 不更狀態，讓下批再試一次
	                continue;
	            }
	        }
	    }
	    
	    
	}


    private void sendEmail(WorkoutPlanVO planVO, Integer planNotifyStatus) {
    	
    	String formatDate = planVO.getWorkoutPlanDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    	String formatTime = planVO.getWorkoutPlanTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    	
        String subject = (WorkoutPlanNotifyStatus.MAKEUP.getCodeNum().equals(planNotifyStatus))
                ? "[ TibaFit 溫馨小提醒 - 補 ] "
                : "[ TibaFit 溫馨小提醒 ] ";
        
        subject += " 計畫: " + planVO.getWorkoutPlanName() + "，預計開始時間: " + formatDate + " " + formatTime;

        String content = "計畫名稱: " + planVO.getWorkoutPlanName() + " \n"
        	    + "預計開始時間: " + formatDate + " " + formatTime + " \n"
        	    + "預計時長: " + planVO.getWorkoutPlanExpectedDuration() + " (min) \n\n"
        	    + "---------------------------\n\n"
        	    + "運動項目: " + resolveSportName(planVO) + " \n"
        	    + "此運動預計消耗熱量: " + estimatedCalories(planVO) + " (kcal / 1 hr)";

        if (WorkoutPlanNotifyStatus.MAKEUP.getCodeNum().equals(planNotifyStatus)) {
            content += "\n\n" + " ※ 此為補寄提醒信，如造成您的任何不便敬請見諒。";
        }
        
        content += "\n\n\n\n" + "Best wishes, \n" + "TibaFit 團隊 :)";

        try {
            MimeMessage mimeMessage = rrooMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            String setndToEmail = resolveUserEmail(planVO);
            helper.setFrom("TibaFit 團隊 <" + sendFromEmail + ">");
            helper.setTo(setndToEmail);
            helper.setSubject(subject);
            helper.setText(content, false);

            // 寄出
            rrooMailSender.send(mimeMessage);
        } catch (MessagingException e) {
        	System.out.println("Svc 寄信失敗: " + e.getMessage());
        }
    }

    private String resolveSportName(WorkoutPlanVO planVO) {
        if (WorkoutPlanSportFrom.SYSTEM.getCodeName().equalsIgnoreCase(planVO.getSportFrom())) {
        	String sportName = planVO.getSportVO() == null || planVO.getSportVO().getSportName() == null ? "未知運動" : planVO.getSportVO().getSportName();
            return sportName;
        } else {
        	String customSportName = planVO.getCustomSportVO() == null || planVO.getCustomSportVO().getSportName() == null ? "未知運動" : planVO.getCustomSportVO().getSportName();
            return customSportName;
        }
    }

    private Integer estimatedCalories(WorkoutPlanVO planVO) {
        if (WorkoutPlanSportFrom.SYSTEM.getCodeName().equalsIgnoreCase(planVO.getSportFrom())) {
        	Integer sportCalorie = planVO.getSportVO() == null || planVO.getSportVO().getSportEstimatedCalories() == null ? 0 : planVO.getSportVO().getSportEstimatedCalories();
            return sportCalorie;
        } else {
        	Integer customSportCalorie = planVO.getCustomSportVO() == null || planVO.getCustomSportVO().getSportEstimatedCalories() == null ? 0 : planVO.getCustomSportVO().getSportEstimatedCalories();
            return customSportCalorie;
        }
    }

    private String resolveUserEmail(WorkoutPlanVO planVO) {
    	String defaultEmail = defaultSendToEmail;
        if(planVO.getUserVO() == null || planVO.getUserVO().getEmail() == null) {
        	return defaultEmail;
        }
        return planVO.getUserVO().getEmail();
    }
}
