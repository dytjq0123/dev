package kr.or.dev.service;

import kr.or.dev.entity.alim.Alim;
import kr.or.dev.repository.AlimRepository;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AlimService {

    @Autowired
    private AlimRepository alimRepository;

    // 회원의 알림 리스트
    public List<Alim> getAlimList(Long mem_no) {
        return alimRepository.findByMemNo(mem_no);
    }

    // 알림 상세 정보
    public Alim getAlimDetail(Long alim_no) throws NotFoundException {
        return alimRepository.findById(alim_no).orElseThrow(() -> new NotFoundException("Not Found Alim Detail"));
    }

    // 알림 생성
    public int insertAlim(Alim alim) {
        Alim saveAlim = alimRepository.save(alim);
        if(saveAlim != null){
            return 1;
        }else {
            return 0;
        }
    }

    // 알림 수정
    public void updateAlim(Long alim_no) throws NotFoundException{
        Alim alim = alimRepository.findById(alim_no).orElseThrow(() -> new NotFoundException("Not Found Alim"));
        alim.updateAlim();
    }

    // 알림 삭제
    public void deleteAlim(Long alim_no) throws NotFoundException{
        alimRepository.delete(
                alimRepository.findById(alim_no).orElseThrow(() -> new NotFoundException("Not Found Alim"))
        );
    }



}
