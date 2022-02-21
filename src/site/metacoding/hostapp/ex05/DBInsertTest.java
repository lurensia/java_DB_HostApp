package site.metacoding.hostapp.ex05;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

// 목적 : DB에 List<Hospital> insert하기
public class DBInsertTest {

    // 몇개 다운로드 받을지 먼저 totalCount 확인하기
    public static int getTotalCount() {

        int totalCount = 0;

        try {

            // 1. URL 주소 만들기 - totalCount 확인용
            StringBuffer sb = new StringBuffer();

            sb.append("http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService");
            sb.append("?serviceKey="); // 서비스키
            sb.append("wJmmW29e3AEUjwLioQR22CpmqS645ep4S8TSlqtSbEsxvnkZFoNe7YG1weEWQHYZ229eNLidnI2Yt5EZ3Stv7g==");
            sb.append("&pageNo=?"); // 몇번째 페이지 인지
            sb.append("1");
            sb.append("&numOfRows=");
            sb.append("2"); // totalCount 체크만 할 것이기 때문에 2개만 받아도 된다. (왜 2개냐면 1개만 받으면 List가 아니라 Object로 받더라)
            sb.append("&_type=");
            sb.append("json"); // 데이터 포맷은 JSON

            // 2. 다운로드 받기 - totalCount 확인용

            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));

            StringBuffer sbDownload = new StringBuffer(); // 통신결과 모아두기
            while (true) {
                String input = br.readLine();
                if (input == null) {
                    break;
                }
                sbDownload.append(input);
            }

            // 3. 검증 - totalCountCheck
            // System.out.println(sb.toString());

            // 4. 파싱
            Gson gson = new Gson();
            ResponseDto responseDto = gson.fromJson(sbDownload.toString(), ResponseDto.class);

            // 5. totalCount 담기
            totalCount = responseDto.getResponse().getBody().getTotalCount();
            System.out.println("totalCount : " + totalCount);
            return totalCount;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCount;
    }

    // KISS 하게 만드는 것이 좋다.
    // totalCount 기반으로 전체 데이터 다운로드해서 자바 오브젝트로 파싱하기
    public static ResponseDto download() {

        ResponseDto responseDto = null;

        try {
            // 6. 전체 데이터 받기
            // (1) URL 주소 만들기
            int totalCount = getTotalCount();

            if (totalCount == 0) {
                System.out.println("totalCount를 제대로 받지 못하였습니다.");
                return null;
            }

            StringBuffer sb = new StringBuffer();

            sb.append("http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService");
            sb.append("?serviceKey="); // 서비스키
            sb.append("wJmmW29e3AEUjwLioQR22CpmqS645ep4S8TSlqtSbEsxvnkZFoNe7YG1weEWQHYZ229eNLidnI2Yt5EZ3Stv7g==");
            sb.append("&pageNo=?"); // 몇번째 페이지 인지
            sb.append("1");
            sb.append("&numOfRows=");
            sb.append(totalCount); // totalCount 체크만 할 것이기 때문에 2개만 받아도 된다. (왜 2개냐면 1개만 받으면 List가 아니라 Object로 받더라)
            sb.append("&_type=");
            sb.append("json"); // 데이터 포맷은 JSON

            // (2) 다운로드 받기
            URL url = new URL(sb.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));

            StringBuffer sbDownload = new StringBuffer(); // 통신결과 모아두기
            while (true) {
                String input = br.readLine();
                if (input == null) {
                    break;
                }
                sbDownload.append(input);
            }

            // (3) 파싱
            Gson gson = new Gson();
            responseDto = gson.fromJson(sbDownload.toString(), ResponseDto.class);

            // 7. 사이즈 검증
            System.out.println("아이템 사이즈 : " + responseDto.getResponse().getBody().getItems().getItem().size());
            System.out.println("totalCount : " + totalCount);
            if (responseDto.getResponse().getBody().getItems().getItem().size() == totalCount) {
                System.out.println("성공~~~~~~~~~~~~~~~~");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseDto;
    }

    public static List<Hospital> migration() {
        ResponseDto responseDto = download();
        // 5682
        List<Item> list = responseDto.getResponse().getBody().getItems().getItem();

        List<Hospital> hospitals = new ArrayList<>(); // hospitals에 list로 옮기면 끝

        for (Item item : list) {
            Hospital hs = new Hospital();
            hs.objectCopy(item);
            hospitals.add(hs);
        }

        // 검증
        System.out.println(hospitals.get(5681).getYadmNm());

        return hospitals;
    }

    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "SCOTT", "TIGER");
            System.out.println("1. DB연결 성공");

            String sql = "INSERT INTO hospital(id, yadmNm, pcrPsblYn, addr) VALUES(seq_hospital.nextval, ?, ?, ?)";

            List<Hospital> hospitals = migration();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (Hospital hospital : hospitals) {
                pstmt.setString(1, hospital.getYadmNm());
                pstmt.setString(2, hospital.getPcrPsblYn());
                pstmt.setString(3, hospital.getAddr());

                // 문제되는 코드 => 한방에 insert문을 전송해서 한번에 commit = 배치프로그램
                // pstmt.executeUpdate();

                pstmt.addBatch(); // 버퍼에 담기
                pstmt.clearParameters(); // 완성된 쿼리를 원복 (이유 : pstmt 한개만 쓸꺼니까)
            }
            pstmt.executeBatch(); // 항아리에 담긴걸 한방에 전송 후 commit
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}