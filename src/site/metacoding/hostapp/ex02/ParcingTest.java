package site.metacoding.hostapp.ex02;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.Gson;

//목적.파싱(json -> java)
public class ParcingTest {
    public static void main(String[] args) {

        // 1.URL주소 만들기(끝)
        StringBuffer sbUrl = new StringBuffer();

        sbUrl.append("http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?");
        sbUrl.append("serviceKey=");// 서비스키
        sbUrl.append("wJmmW29e3AEUjwLioQR22CpmqS645ep4S8TSlqtSbEsxvnkZFoNe7YG1weEWQHYZ229eNLidnI2Yt5EZ3Stv7g%3D%3D");
        sbUrl.append("&pageNo=");// 페이지넘버
        sbUrl.append("1");
        sbUrl.append("&numOfRows=");
        sbUrl.append("10");// 한페이지에 몇개 씩 가져올지
        sbUrl.append("&type=");
        sbUrl.append("json");// 데이터 포맷은 json

        // 2.다운로드 받기(끝)
        try {
            // URL세이프가 적용 되어 있을 때에 세이프가 되어 있으면 더 이상 변경 안함
            URL url = new URL(sbUrl.toString());
            HttpURLConnection conn = (HttpURLConnection) url.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

            StringBuffer sbDownload = new StringBuffer();// 통신 결과 모아두기
            while (true) {
                String input = br.readLine();
                if (input == null) {
                    break;
                }
                sbDownload.append(input);
            }

            // 검증
            // System.out.println(sbDownload.toString());

            // 파싱
            Gson gson = new Gson();
            ResponseDto responseDto = gson.fromJson(sbDownload.toString(), ResponseDto.class);

            // 4.검증
            int itemSize = responseDto.getResponse().getBody().getItems().getItem().size();
            System.out.println("아이템 사이즈:" + itemSize);
            System.out.println(responseDto.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
