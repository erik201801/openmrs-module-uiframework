package org.openmrs.ui2.core.fragment;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.ui2.core.AttributeExpressionException;
import org.openmrs.ui2.core.AttributeHolder;
import org.openmrs.ui2.core.AttributeHolderUtil;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

public class FragmentActionRequest implements AttributeHolder {
	
	FragmentFactory factory;
	
	FragmentActionUiUtils ui;
	
	HttpServletRequest httpRequest;
	
	Errors errors;
	
	public FragmentActionRequest(FragmentFactory factory, HttpServletRequest httpRequest) {
		this.factory = factory;
		this.httpRequest = httpRequest;
		errors = new MapBindingResult(httpRequest.getParameterMap(), "request");
		this.ui = new FragmentActionUiUtils(factory.getMessageSource(), factory.getExtensionManager(), factory
		        .getConversionService());
	}
	
	public FragmentActionUiUtils getUiUtils() {
		return ui;
	}
	
	public FragmentFactory getFactory() {
		return factory;
	}
	
	public boolean hasErrors() {
		return errors.hasErrors();
	}
	
	public String getParameter(String name) {
		return httpRequest.getParameter(name);
	}
	
	public String getRequiredParameter(String name, String label) {
		String ret = getParameter(name);
		if (ret == null || ret.equals(""))
			errors.rejectValue(name, null, ui.message("error.required"));
		return ret;
	}
	
	public <T> T getParameter(String name, Class<T> asType) {
		String s = getParameter(name);
		try {
			return factory.convert(s, asType);
		}
		catch (RuntimeException ex) {
			if (StringUtils.isEmpty(s))
				return null;
			throw ex;
		}
	}
	
	public <T> T getRequiredParameter(String name, Class<T> asType, String label) {
		T ret = getParameter(name, asType);
		if (ret == null || "".equals(ret))
			errors.rejectValue(name, null, ui.message("error.required"));
		return ret;
	}
	
	public void globalError(String message) {
		errors.reject(null, message);
	}
	
	public void fieldError(String fieldName, String message) {
		errors.rejectValue(fieldName, null, message);
	}
	
	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}
	
	public Errors getErrors() {
		return errors;
	}
	
	/**
	 * @see org.openmrs.ui2.core.AttributeHolder#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		return getParameter(name);
	}
	
	/**
	 * @see org.openmrs.ui2.core.AttributeHolder#require(java.lang.String[])
	 */
	@Override
	public void require(String... expressions) throws AttributeExpressionException {
		List<String> failed = AttributeHolderUtil.unsatisfiedExpressions(this, expressions);
		if (failed.size() > 0) {
			throw new AttributeExpressionException(expressions, failed);
		}
	}
	
}
