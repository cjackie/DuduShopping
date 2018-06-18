package com.dudu.shipping.fedex;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "TrackRequest")
public class TrackRequest implements RequestBody, RequestBuilder {

    @Element(name = Request.VERSION + ":PackageIdentifier")
    private PackageIdentifier packageIdentifier;

    @Element(name = Request.VERSION + ":SelectionDetail")
    private SelectionDetail selectionDetail;

    @Element(name = Request.VERSION + ":WebAuthenticationCredential")
    private WebAuthenticationCredential webAuthenticationCredential;

    @Element(name = Request.VERSION + ":ClientDetail")
    private ClientDetail clientDetail;

    public PackageIdentifier getPackageIdentifier() {
        return packageIdentifier;
    }

    public void setPackageIdentifier(PackageIdentifier packageIdentifier) {
        this.packageIdentifier = packageIdentifier;
    }

    public SelectionDetail getSelectionDetail() {
        return selectionDetail;
    }

    public void setSelectionDetail(SelectionDetail selectionDetail) {
        this.selectionDetail = selectionDetail;
    }

    public WebAuthenticationCredential getWebAuthenticationCredential() {
        return webAuthenticationCredential;
    }

    public void setWebAuthenticationCredential(WebAuthenticationCredential webAuthenticationCredential) {
        this.webAuthenticationCredential = webAuthenticationCredential;
    }

    public ClientDetail getClientDetail() {
        return clientDetail;
    }

    public void setClientDetail(ClientDetail clientDetail) {
        this.clientDetail = clientDetail;
    }

    /**
     *
     * @param trackingNumber
     * @param key
     * @param password
     * @param accountNumber
     * @param meterNumber
     * @return
     */
    public static Request buildRequest(String trackingNumber, String key, String password, String accountNumber, String meterNumber) {
        TrackRequest trackRequest = new TrackRequest();

        PackageIdentifier identifier = new PackageIdentifier();
        identifier.setType(PackageIdentifier.TRACKING_NUMBER_OR_DOORTAG);

        SelectionDetail selectionDetail = new SelectionDetail();
        selectionDetail.setProcessingOptions(SelectionDetail.PROCESSING_OPTIONS_INCLUDE_DETAILED_SCANS);
        selectionDetail.setTrackingNumberUniqueIdentifier(trackingNumber);

        trackRequest.setPackageIdentifier(identifier);
        trackRequest.setSelectionDetail(selectionDetail);

        WebAuthenticationCredential authenticationCredential = new WebAuthenticationCredential();
        UserCredential userCredential = new UserCredential();
        userCredential.setKey(key);
        userCredential.setPassword(password);
        authenticationCredential.setUserCredential(userCredential);

        trackRequest.setWebAuthenticationCredential(authenticationCredential);

        ClientDetail clientDetail = new ClientDetail();
        clientDetail.setAccountNumber(accountNumber);
        clientDetail.setMeterNumber(meterNumber);

        trackRequest.setClientDetail(clientDetail);

        return Request.buildRequest(trackRequest);
    }

}
