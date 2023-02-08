package captcha;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import nl.captcha.Captcha;

/**
 * Servlet implementation class CaptchaControl
 */
@WebServlet("/CaptchaControl")
public class CaptchaControl extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CaptchaControl() {
        super();
        
        // TODO Auto-generated constructor stub
    }

	/**
	 * @return 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");

		JSONObject json = new JSONObject();
		String func = request.getParameter("func");
		System.out.println("innn");
		switch(func){
		case "img" : 
			new CaptchaUtil().getImgCaptCha(request, response);
		break;
		case "audio" :
			Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
			String getAnswer = captcha.getAnswer();
			new CaptchaUtil().getAudioCaptCha(request, response, getAnswer);
		break;
		case "html" :
			String html = "<label for='captcha' style='display:block'>자동 로그인 방지</label>"
					+ "<div style='overflow:hidden'>"
					+ "<div style='float:left'>"
					+ "<img title='캡차이미지' src='/captchaImg.do' alt='캡차이미지'/>"
					+ "<div id='ccaudio' style='display:none'></div>"
					+ "</div>"
					+ "<div style='float:right'>"
					+ "<input id='reload' type='button' onclick='getImage()' value='새로고침'/>"
					+ "<input id='soundOn' type='button' onclick='audio(0)' value='영어음성'/>"
					+ "<input id='soundOn' type='button' onclick='audio(1)' value='한글음성'/>"
					+ "</div>"
					+ "</div>";
			json.put("html", html);
			response.getWriter().print(json);
		break;
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	public boolean captchaChk(HttpServletRequest request, HttpServletResponse response){
		boolean result = false;
		Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
		String ans = request.getParameter("answer");
//		System.out.println(ans);
		if(ans!=null && !"".equals(ans)) {
			System.out.println(captcha);
			if(captcha.isCorrect(ans)) {
				//일치하면 세션에서 지우기.
				request.getSession().removeAttribute(Captcha.NAME);
				result = true;
			}else {
				result = false;
			}
		}
		return result;
	}
	
	
	
}
