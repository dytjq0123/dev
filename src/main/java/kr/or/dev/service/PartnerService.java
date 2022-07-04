package kr.or.dev.service;

import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.partner.Partner;
import kr.or.dev.repository.MemberRepository;
import kr.or.dev.repository.PartnerRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PartnerService {

    private final PartnerRepository partnerRepository;

    private final MemberRepository memberRepository;

    public int insertPtn(Partner partner) {
        Partner savePtn = partnerRepository.save(partner);

        if(savePtn != null){
            return 1;
        }else {
            return 0;
        }
    }

    public void updatePtn(Long ptn_no) throws NotFoundException {
        Partner findPartner = partnerRepository.findById(ptn_no).orElseThrow(() -> new NotFoundException("Not Found Partner"));

        findPartner.updatePtn();
    }

    public List<Partner> getMyPtnList(Long mem_no) {
        Member member = memberRepository.findById(mem_no).get();

        List<Partner> ptnMyList = partnerRepository.getPtnMyList(member);

        ptnMyList.forEach(partner -> {
            if(partner.getPtn_apply().getMem_no() != member.getMem_no()){
                partner.setMember(partner.getPtn_apply());
            }else if(partner.getPtn_accept().getMem_no() != member.getMem_no()){
                partner.setMember(partner.getPtn_accept());
            }
        });

        return ptnMyList;
    }
}
