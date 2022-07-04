package kr.or.dev.service;

import kr.or.dev.entity.member.Member;
import kr.or.dev.entity.timeline.TimeLine;
import kr.or.dev.entity.timeline.emoticon.Emoticon;
import kr.or.dev.repository.EmoticonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class EmoticonService {

    private final EmoticonRepository emoticonRepository;

    public List<Emoticon> getEmoAllList() {
        List<Emoticon> allList = emoticonRepository.findAll();

        return allList;
    }

    public Emoticon getEmoDetail(Long emo_no) {
        Emoticon emoticon = emoticonRepository.findById(emo_no).get();

        return emoticon;
    }

    public long insertEmoUser(Long emo_no, Map paramMap) {
        Emoticon emoticon = emoticonRepository.findById(emo_no).get();
        emoticon.addEmoUser((Member) paramMap.get("member"), (TimeLine) paramMap.get("timeLine"));

        return ((Member) paramMap.get("member")).getMem_no();
    }

    public void deleteEmoUser(Long emo_no, Map paramMap) {
        Emoticon emoticon = emoticonRepository.findById(emo_no).get();
        emoticon.remEmoUser((Member) paramMap.get("member"), (TimeLine) paramMap.get("timeLine"));

    }
}
