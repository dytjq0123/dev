package kr.or.dev.service;

import kr.or.dev.dto.mapper.FilesMapperDto;
import kr.or.dev.entity.files.Files;
import kr.or.dev.repository.FilesRepository;
import kr.or.dev.repository.custom.FilesRepositoryCustomImpl;
import kr.or.dev.repository.mapper.FilesMapperRepository;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class FilesService {

    private final FilesRepository filesRepository;

    private final FilesRepositoryCustomImpl filesRepositoryCustom;

    private final FilesMapperRepository filesMapperRepository;

    // 파일 생성
    public long insertFiles(Files files) {
        Files saveFile = filesRepository.save(files);

        return saveFile.getFiles_no();
    }

    // 파일 삭제
    public void deleteFiles(Long files_no) throws NotFoundException {
        Files findFile = filesRepository.findById(files_no).orElseThrow(() -> new NotFoundException("Not Found File"));

        // 저장된 파일 삭제
        File savedFile = new File(findFile.getFiles_path(), findFile.getFiles_name());
        savedFile.delete();

        // db의 파일 데이터 삭제
        filesRepository.delete(findFile);

    }

    // 파일 상세정보
    public Files getFilesDetail(Long files_no) {
        Files findFile = filesRepository.findById(files_no).get();

        return findFile;
    }

    public List<Files> getFilesList(Map paramMap) {
        List<Files> filesList = filesRepositoryCustom.getFilesList(paramMap);

        return filesList;
    }

    public List<FilesMapperDto> getFilesSearchList(Map paramMap) {
        List<FilesMapperDto> filesList = filesMapperRepository.getFilesSearchList(paramMap);

        return filesList;
    }

    public List<FilesMapperDto> getFilesBaTaAllList(Map paramMap) {
        List<FilesMapperDto> filesList = filesMapperRepository.getFilesBaTaAllList(paramMap);

        return filesList;
    }

    public List<FilesMapperDto> getFilesRepAllList(Map paramMap) {
        List<FilesMapperDto> filesList = filesMapperRepository.getFilesRepAllList(paramMap);

        return filesList;
    }

    public List<FilesMapperDto> getProFilesBaTaAllList(Map paramMap) {
        List<FilesMapperDto> filesList = filesMapperRepository.getProFilesBaTaAllList(paramMap);

        return filesList;
    }

    public List<FilesMapperDto> getProFilesRepAllList(Map paramMap) {
        List<FilesMapperDto> filesList = filesMapperRepository.getProFilesRepAllList(paramMap);

        return filesList;
    }






}
