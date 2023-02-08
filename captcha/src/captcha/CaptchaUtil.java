package captcha;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.captcha.Captcha;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.audio.producer.VoiceProducer;
import nl.captcha.backgrounds.GradiatedBackgroundProducer;
import nl.captcha.exp.SetKorVoiceProducer;
import nl.captcha.servlet.CaptchaServletUtil;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.renderer.DefaultWordRenderer;

public class CaptchaUtil {
	private static int width = 150;	/*보안문자 이미지 가로크기*/
	private static int height = 50; /*보안문자 이미지 세로크기*/
	
	/*CaptCha Image 생성*/
	public void getImgCaptCha(HttpServletRequest req, HttpServletResponse res) {
		/*폰트 및 컬러 설정*/
		List<Font> fontList = new ArrayList<Font>();
		fontList.add(new Font("", Font.HANGING_BASELINE, 40));
		fontList.add(new Font("Courier", Font.ITALIC, 40));
		fontList.add(new Font("", Font.PLAIN, 40));
		List<Color> colorList = new ArrayList<Color>();
		colorList.add(Color.BLACK);
		
		Captcha captcha = new Captcha.Builder(width,  height)
				/* .addText() 또는 아래와 같이 정의 : 6자리 숫자와 폰트 및 컬러 설정
				 * 디폴트로 5개의 랜덤한 알파뱃과 숫자를 생성
				 * */
				.addText(new NumbersAnswerProducer(4), new DefaultWordRenderer(colorList, fontList))
				.addNoise() // 한 번 호출할 때마다 하나의 라인이 추가된다.
				.addNoise() 
				.addBorder() //검정 테두리 선 추가
				.addBackground(new GradiatedBackgroundProducer()) //그라디언트 백그라운드 추가.
				.build();
		
		
		res.setHeader("Pragma", "no-cache"); //브라우저의 캐쉬를 지우기 위한 헤더값 설정
		//Captcha.NAME
		/*JSP에서 Captcha 객체에 접근할 수 있도록 session에 저장*/
		//새로고침 시 세션에서 캡챠 지우기
		req.getSession().removeAttribute(Captcha.NAME);
		req.getSession().setAttribute(Captcha.NAME, captcha);
		CaptchaServletUtil.writeImage(res,  captcha.getImage());
	}
	
	/*CaptCha Audio 생성*/
	public void getAudioCaptCha(HttpServletRequest req, HttpServletResponse res, String answer) throws IOException {
	 	HttpSession session = req.getSession();
        Captcha captcha = (Captcha) session.getAttribute(Captcha.NAME);
        String getAnswer = answer;
        AudioCaptcha audiocaptcha = null;
        ///주선
        if ( getAnswer == null || "".equals(getAnswer) ) getAnswer = captcha.getAnswer();
        
        String lan = req.getParameter("lan");
        if( lan != null && "kor".equals(lan)) {
        	VoiceProducer vProd = new SetKorVoiceProducer(); //한글 음성을 생성해주는 객체 생성
            audiocaptcha = new AudioCaptcha.Builder()
            .addAnswer(new SetTextProducer(getAnswer))
            .addVoice(vProd) //한글음성생성기를 AudioCaptcha에 적용
//            .addNoise()
            .build();
        } else {   
        	 audiocaptcha = new AudioCaptcha.Builder()
            .addAnswer(new SetTextProducer(getAnswer))
//            .addNoise()
            .build();
        }
        CaptchaServletUtil.writeAudio(res, audiocaptcha.getChallenge());
    }
}