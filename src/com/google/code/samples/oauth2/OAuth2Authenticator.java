package com.google.code.samples.oauth2;

import java.security.Provider;
import java.security.Security;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Session;
import javax.mail.URLName;


//import com.sun.mail.auth.OAuth2SaslClientFactory;
import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.imap.IMAPStore;



public class OAuth2Authenticator {
	  private static final Logger logger =
	      Logger.getLogger(OAuth2Authenticator.class.getName());

	  public static final class OAuth2Provider extends Provider {
	    private static final long serialVersionUID = 1L;

	    public OAuth2Provider() {
	      super("Google OAuth2 Provider", 1.0,
	            "Provides the XOAUTH2 SASL Mechanism");
	      put("SaslClientFactory.XOAUTH2",
	          "com.google.code.samples.oauth2.OAuth2SaslClientFactory");
	    }
	  }

	  /**
	   * Installs the OAuth2 SASL provider. This must be called exactly once before
	   * calling other methods on this class.
	   */
	  public static void initialize() {
	    Security.addProvider(new OAuth2Provider());
	  }

	  /**
	   * Connects and authenticates to an IMAP server with OAuth2. You must have
	   * called {@code initialize}.
	   *
	   * @param host Hostname of the imap server, for example {@code
	   *     imap.googlemail.com}.
	   * @param port Port of the imap server, for example 993.
	   * @param userEmail Email address of the user to authenticate, for example
	   *     {@code oauth@gmail.com}.
	   * @param oauthToken The user's OAuth token.
	   * @param debug Whether to enable debug logging on the IMAP connection.
	   *
	   * @return An authenticated IMAPStore that can be used for IMAP operations.
	   */
	  public static IMAPStore connectToImap(String host,
	                                        int port,
	                                        String userEmail,
	                                        String oauthToken,
	                                        boolean debug) throws Exception {
	    Properties props = new Properties();
	    props.put("mail.imaps.sasl.enable", "true");
	    props.put("mail.imaps.sasl.mechanisms", "XOAUTH2");
	    props.put(OAuth2SaslClientFactory.OAUTH_TOKEN_PROP, oauthToken);
	    Session session = Session.getInstance(props);
	    session.setDebug(debug);

	    final URLName unusedUrlName = null;
	    IMAPSSLStore store = new IMAPSSLStore(session, unusedUrlName);
	    final String emptyPassword = "";
	    store.connect(host, port, userEmail, emptyPassword);
	    return store;
	  }
}