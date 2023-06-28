package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
        UUID uuid = UUID.randomUUID();//개체구별위한 유니크 ID 메이커
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;//UUID 값과 파일이름 확장자 조합으로 이름을 만듦.
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);//byte 단위 출력 클래스,
        //생성자로 파일이 저장될 위치와 파일의 이름을 넘겨 파일에 쓸 파일 출력 스트림을 만듦.
        fos.write(fileData);//바이트배열인 fileData 를 파일 출력 스트림에 입력함.
        fos.close();
        return savedFileName;//파일이름 반환
    }
    public void deleteFile(String filePath) throws Exception{
        File deleteFile = new File(filePath); //파일경로로 파일객체 생성
        if(deleteFile.exists()){//그파일이 있다면 삭제한다.
            deleteFile.delete();
            log.info("파일을 삭제했습니다.");
        }else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
