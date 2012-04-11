package org.zy.examples.shiro.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ContainerIntegrationTest extends AbstractContainerTest {
	
	@Before
	public void logOut() throws Exception {
		final HtmlPage homePage = webClient.getPage(getBaseUri());
		try {
			homePage.getAnchorByHref("/logout").click();
		}catch(ElementNotFoundException e) {}
	}
	
	@Test
	public void logIn() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		HtmlPage page = webClient.getPage(getBaseUri() + "login.jsp");
		HtmlForm form = page.getFormByName("loginform");
		form.getInputByName("username").setValueAttribute("root");
		form.getInputByName("password").setValueAttribute("secret");
		page = form.getInputByName("submit").click();
		page.getAnchorByHref("/logout");
	}
	
	@Test
	public void logInAndRemermberMe() throws Exception {
		HtmlPage page = webClient.getPage(getBaseUri() + "login.jsp");
		HtmlForm form = page.getFormByName("loginform");
		form.getInputByName("username").setValueAttribute("root");
		form.getInputByName("password").setValueAttribute("secret");
		HtmlCheckBoxInput checkbox = form.getInputByName("rememberMe");
		checkbox.setChecked(true);
		page = form.getInputByName("submit").click();
		server.stop();
		server.start();
		page = webClient.getPage(getBaseUri());
		WebAssert.assertLinkPresentWithText(page, "Log out");
		page = page.getAnchorByHref("/account").click();
		WebAssert.assertFormPresent(page, "loginform");
	}

}
