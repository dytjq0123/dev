package kr.or.dev.service;

import kr.or.dev.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void save() {
    }

    @Test
    void changePw() {
    }

    @Test
    void getMemberDetail() {
    }

    @Test
    void chMemId() {
        String mem_id = "test@gmail.com";

        int i = memberRepository.idChk(mem_id);

        System.out.println("i = " + i);
    }

    @Test
    void chkMemNick() {
    }

    @Test
    void getMemAllList() {
    }

    @Test
    void findId() {
    }

    @Test
    void getMemChk() {
    }

    @Test
    void getMemAllCnt() {
    }

    @Test
    void getMemHowjoinCnt() {
    }

    @Test
    void findAll() {
    }
}