package com.mylab.wicket.jpa.ui.application;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.JavaScriptFilteredIntoFooterHeaderResponse;
import org.apache.wicket.markup.html.IHeaderResponseDecorator;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import com.mylab.wicket.jpa.ui.pages.error.ExpiredPage;
import com.mylab.wicket.jpa.ui.pages.error.NotAllowedPage;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 *
 * @see com.mylab.wicket.Start#main(String[])
 */
public class WicketApplication extends AuthenticatedWebApplication {

    /**
     * Constructor.
     */
    public WicketApplication() {
    	
    }
	
    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        
    	return SignIn.class;
    }
    
    /**
     * @see org.apache.wicket.protocol.http.WebApplication#newSession(Request, Response)
     */
    @Override
    public Session newSession(Request request, Response response) { 
    	
        return new SignInSession(request);
    }
    
	@Override
	protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
		
		return SignInSession.class;
	}

	@Override
	protected Class<? extends WebPage> getSignInPageClass() {
		
		return SignIn.class;
	}

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
    	
        super.init();
		setHeaderResponseDecorator(new JavaScriptToBucketResponseDecorator("footer-container"));

        // Register the authorization strategy 
        getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(this));
        
        // Set different error pages
        getApplicationSettings().setAccessDeniedPage(NotAllowedPage.class);
        getApplicationSettings().setPageExpiredErrorPage(ExpiredPage.class);
                
        getSecuritySettings().setUnauthorizedComponentInstantiationListener(new IUnauthorizedComponentInstantiationListener() {

    				@Override public void onUnauthorizedInstantiation(Component component) {
    					component.setResponsePage(NotAllowedPage.class); 
    				} 
    	});
        
        // Turn off Ajax debug settings in browser:
        getDebugSettings().setAjaxDebugModeEnabled(false); 
    }
    
	/**
     * Decorates an original {@link org.apache.wicket.markup.head.IHeaderResponse} and renders all javascript items
     * (JavaScriptHeaderItem), to a specific container in the page.
     */
    static class JavaScriptToBucketResponseDecorator implements IHeaderResponseDecorator 
    {

        private String bucketName;

        public JavaScriptToBucketResponseDecorator(String bucketName) {
            this.bucketName = bucketName;
        }

        @Override
        public IHeaderResponse decorate(IHeaderResponse response) {
            return new JavaScriptFilteredIntoFooterHeaderResponse(response, bucketName);
        }

    }

}