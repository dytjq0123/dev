package kr.or.dev.service;

import kr.or.dev.dto.kind.KindUpdateDto;
import kr.or.dev.entity.group.kind.Kind;
import kr.or.dev.repository.KindRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class KindService {

    private final KindRepository kindRepository;

    public int insertKind(Kind kind) {
        Kind saveKind = kindRepository.save(kind);

        if(saveKind != null){
            return 1;
        }else {
            return 0;
        }
    }

    public void updateKind(KindUpdateDto kindUpdateDto) throws NotFoundException {
        Kind findKind = kindRepository.findById(kindUpdateDto.getKind_no()).orElseThrow(() -> new NotFoundException("Not Found Kind"));
        findKind.updateKind(kindUpdateDto.getKind_name());
    }

    public void deleteKind(Long kind_no) throws NotFoundException {
        Kind findKind = kindRepository.findById(kind_no).orElseThrow(() -> new NotFoundException("Not Found Kind"));
        findKind.deleteKind();
    }

    public List<Kind> getKindAllList() {
        List<Kind> kindList = kindRepository.findAll();

        return kindList;
    }

    public Kind getKindDetail(Long kind_no) {
        Kind kind = kindRepository.findById(kind_no).get();

        return kind;
    }
}
