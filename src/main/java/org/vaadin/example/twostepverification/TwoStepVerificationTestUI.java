package org.vaadin.example.twostepverification;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;

import javax.servlet.annotation.WebServlet;

@Theme("runo")
@Title("Two-step authentication test")
public class TwoStepVerificationTestUI extends UI {

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = TwoStepVerificationTestUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }

    private VerticalLayout root = new VerticalLayout();

    private FormLayout formLayout = new FormLayout();

    private CssLayout resultLayout = new CssLayout();

    private String userName;

    private String issuer;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        root.setMargin(true);
        root.setSpacing(true);
        setContent(root);

        Label titleLabel = new Label(
            "<h1>Two-step authentication test</h1>",
            ContentMode.HTML);
        root.addComponent(titleLabel);
        initUserInfoForm();
    }

    private void initUserInfoForm() {
        TextField issuerField = new TextField("Service issuer");
        issuerField.setImmediate(true);
        issuerField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                String value = event.getText();
                if (value != null && !value.isEmpty()) {
                    issuer = value;
                }
            }
        });

        TextField userField = new TextField("User name");
        userField.setImmediate(true);
        userField.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                String value = event.getText();
                if (value != null && !value.isEmpty()) {
                    userName = value;
                }
            }
        });

        Button generateButton = new Button("Generate",
            new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    initResultLayout();
                }
            });
        generateButton.setImmediate(true);
        formLayout.addComponents(issuerField, userField, generateButton);
        formLayout.setSpacing(true);
        root.addComponent(formLayout);

    }

    private void initResultLayout() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String optAuthUrl = GoogleAuthenticatorQRGenerator
            .getOtpAuthURL(issuer, userName, key);

        resultLayout.removeAllComponents();

        Label keyLabel = new Label("", ContentMode.HTML);
        keyLabel.setValue(String.format("<b>Key:</b> %s", key.getKey()));

        Label qrLinkLabel = new Label("", ContentMode.HTML);
        qrLinkLabel.setValue("<b>QR code address:</b> <a href=\"" + optAuthUrl
            + "\" target=\"_blank\">" + optAuthUrl + "</a>");

        Image qrImage = new Image(null, new ExternalResource(optAuthUrl));
        qrImage.setSource(new ExternalResource(optAuthUrl));

        resultLayout.addComponents(keyLabel, qrLinkLabel, qrImage);
        root.addComponent(resultLayout);
    }

}
