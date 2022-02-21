package site.metacoding.hostapp.ex01;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// 테스트 주도 개발!!
// 목적 : 다운로드
public class DownloadTest {

    public static void main(String[] args) {

        // 1. URL 주소 만들기
        StringBuffer sbUrl = new StringBuffer();

        sbUrl.append("http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService");
        sbUrl.append("?serviceKey="); // 서비스 키
        sbUrl.append("서비스키%3D%3D");
        sbUrl.append("&pageNo="); // 몇번째 페이지 인지
        sbUrl.append("1");
        sbUrl.append("&numOfRows=");
        sbUrl.append("10"); // 한 페이지당 몇개씩 가져올지
        sbUrl.append("&_type=");
        sbUrl.append("json"); // 데이터 포맷은 JSON

        // 2. 다운로드 받기
        try {
            URL url = new URL(sbUrl.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));

            StringBuffer sbDownload = new StringBuffer(); // 통신결과 모아두기

            while (true) {
                String input = br.readLine();

                // 버퍼가 비었을 때 break
                if (input == null) {
                    break;
                }

                sbDownload.append(input);
            }

            // 3. 검증
            System.out.println(sbDownload.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}