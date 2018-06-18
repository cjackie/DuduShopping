package com.dudu.shipping;

import com.dudu.shipping.fedex.*;
import org.junit.Test;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.OutputStreamWriter;

public class FedexElementsTest {

    @Test
    public void trackRequest() throws Exception {
        TrackRequest request = new TrackRequest();

        PackageIdentifier identifier = new PackageIdentifier();
        identifier.setType(PackageIdentifier.TRACKING_NUMBER_OR_DOORTAG);

        SelectionDetail selectionDetail = new SelectionDetail();
        selectionDetail.setProcessingOptions(SelectionDetail.PROCESSING_OPTIONS_INCLUDE_DETAILED_SCANS);
        selectionDetail.setTrackingNumberUniqueIdentifier("1234");

        request.setPackageIdentifier(identifier);
        request.setSelectionDetail(selectionDetail);

        WebAuthenticationCredential authenticationCredential = new WebAuthenticationCredential();
        UserCredential userCredential = new UserCredential();
        userCredential.setKey("123");
        userCredential.setPassword("456");
        authenticationCredential.setUserCredential(userCredential);

        request.setWebAuthenticationCredential(authenticationCredential);

        ClientDetail clientDetail = new ClientDetail();
        clientDetail.setAccountNumber("acount number");
        clientDetail.setMeterNumber("meter number");

        request.setClientDetail(clientDetail);

        Serializer serializer = new Persister();
        serializer.write(request, new OutputStreamWriter(System.out));
    }

    @Test
    public void buildTrackRequest() throws Exception {
        Request request = TrackRequest.buildRequest("tracking number", "key", "password", "account number", "meter number");
        Serializer serializer = new Persister();
        serializer.write(request, new OutputStreamWriter(System.out));
    }

    @Test
    public void traceReply() throws Exception {
        String reply = "<TrackReply\n" +
                "    xmlns=\"http://fedex.com/ws/track/v14\">\n" +
                "    <HighestSeverity>SUCCESS</HighestSeverity>\n" +
                "    <Notifications>\n" +
                "        <Severity>SUCCESS</Severity>\n" +
                "        <Source>trck</Source>\n" +
                "        <Code>0</Code>\n" +
                "        <Message>Request was successfully processed.</Message>\n" +
                "        <LocalizedMessage>Request was successfully processed.</LocalizedMessage>\n" +
                "    </Notifications>\n" +
                "    <TransactionDetail>\n" +
                "        <CustomerTransactionId>Track By Number_v14</CustomerTransactionId>\n" +
                "        <Localization>\n" +
                "            <LanguageCode>EN</LanguageCode>\n" +
                "            <LocaleCode>US</LocaleCode>\n" +
                "        </Localization>\n" +
                "    </TransactionDetail>\n" +
                "    <Version>\n" +
                "        <ServiceId>trck</ServiceId>\n" +
                "        <Major>14</Major>\n" +
                "        <Intermediate>0</Intermediate>\n" +
                "        <Minor>0</Minor>\n" +
                "    </Version>\n" +
                "    <CompletedTrackDetails>\n" +
                "        <HighestSeverity>SUCCESS</HighestSeverity>\n" +
                "        <Notifications>\n" +
                "            <Severity>SUCCESS</Severity>\n" +
                "            <Source>trck</Source>\n" +
                "            <Code>0</Code>\n" +
                "            <Message>Request was successfully processed.</Message>\n" +
                "            <LocalizedMessage>Request was successfully processed.</LocalizedMessage>\n" +
                "        </Notifications>\n" +
                "        <DuplicateWaybill>false</DuplicateWaybill>\n" +
                "        <MoreData>false</MoreData>\n" +
                "        <TrackDetailsCount>0</TrackDetailsCount>\n" +
                "        <TrackDetails>\n" +
                "            <Notification>\n" +
                "                <Severity>SUCCESS</Severity>\n" +
                "                <Source>trck</Source>\n" +
                "                <Code>0</Code>\n" +
                "                <Message>Request was successfully processed.</Message>\n" +
                "                <LocalizedMessage>Request was successfully processed.</LocalizedMessage>\n" +
                "            </Notification>\n" +
                "            <TrackingNumber>794887075005</TrackingNumber>\n" +
                "            <TrackingNumberUniqueIdentifier>XXXXXXXXXX~XXXXXXXXXXXX~FX\n" +
                "            </TrackingNumberUniqueIdentifier>\n" +
                "            <StatusDetail>\n" +
                "                <CreationTime>2016-11-17T00:00:00</CreationTime>\n" +
                "                <Code>OC</Code>\n" +
                "                <Description>Shipment information sent to FedEx</Description>\n" +
                "                <Location>\n" +
                "                    <Residential>false</Residential>\n" +
                "                </Location>\n" +
                "                <AncillaryDetails>\n" +
                "                    <Reason>IN001</Reason>\n" +
                "                    <ReasonDescription>Please check back later for shipment status or\n" +
                "subscribe for e-mail notifications</ReasonDescription>\n" +
                "                </AncillaryDetails>\n" +
                "            </StatusDetail>\n" +
                "            <ServiceCommitMessage>Shipping label has been created. The status will be\n" +
                "updated when shipment begins to travel.</ServiceCommitMessage>\n" +
                "            <DestinationServiceArea>OC</DestinationServiceArea>\n" +
                "            <CarrierCode>FDXE</CarrierCode>\n" +
                "            <OperatingCompanyOrCarrierDescription>FedEx\n" +
                "Express</OperatingCompanyOrCarrierDescription>\n" +
                "            <OtherIdentifiers>\n" +
                "                <PackageIdentifier>\n" +
                "                    <Type>INVOICE</Type>\n" +
                "                    <Value>IO10570705</Value>\n" +
                "                </PackageIdentifier>\n" +
                "            </OtherIdentifiers>\n" +
                "            <OtherIdentifiers>\n" +
                "                <PackageIdentifier>\n" +
                "                    <Type>PURCHASE_ORDER</Type>\n" +
                "                    <Value>PO10570705</Value>\n" +
                "                </PackageIdentifier>\n" +
                "            </OtherIdentifiers>\n" +
                "            <OtherIdentifiers>\n" +
                "                <PackageIdentifier>\n" +
                "                    <Type>SHIPPER_REFERENCE</Type>\n" +
                "                    <Value>CUSTREF10570705</Value>\n" +
                "                </PackageIdentifier>\n" +
                "            </OtherIdentifiers>\n" +
                "            <Service>\n" +
                "                <Type>PRIORITY_OVERNIGHT</Type>\n" +
                "                <Description>FedEx Priority Overnight</Description>\n" +
                "                <ShortDescription>PO</ShortDescription>\n" +
                "            </Service>\n" +
                "            <PackageWeight>\n" +
                "                <Units>LB</Units>\n" +
                "                <Value>60.0</Value>\n" +
                "            </PackageWeight>\n" +
                "            <PackageDimensions>\n" +
                "                <Length>12</Length>\n" +
                "                <Width>12</Width>\n" +
                "                <Height>12</Height>\n" +
                "                <Units>IN</Units>\n" +
                "            </PackageDimensions>\n" +
                "            <ShipmentWeight>\n" +
                "                <Units>LB</Units>\n" +
                "                <Value>60.0</Value>\n" +
                "            </ShipmentWeight>\n" +
                "            <Packaging>Your Packaging</Packaging>\n" +
                "            <PackagingType>YOUR_PACKAGING</PackagingType>\n" +
                "            <PackageSequenceNumber>1</PackageSequenceNumber>\n" +
                "            <PackageCount>1</PackageCount>\n" +
                "            <SpecialHandlings>\n" +
                "                <Type>DELIVER_WEEKDAY</Type>\n" +
                "                <Description>Deliver Weekday</Description>\n" +
                "                <PaymentType>OTHER</PaymentType>\n" +
                "            </SpecialHandlings>\n" +
                "            <Payments>\n" +
                "                <Classification>TRANSPORTATION</Classification>\n" +
                "                <Type>SHIPPER_ACCOUNT</Type>\n" +
                "                <Description>Shipper</Description>\n" +
                "            </Payments>\n" +
                "            <ShipperAddress>\n" +
                "                <City>COLORADO SPRINGS</City>\n" +
                "                <StateOrProvinceCode>CO</StateOrProvinceCode>\n" +
                "                <CountryCode>US</CountryCode>\n" +
                "                <CountryName>United States</CountryName>\n" +
                "                <Residential>false</Residential>\n" +
                "            </ShipperAddress>\n" +
                "            <DatesOrTimes>\n" +
                "                <Type>ANTICIPATED_TENDER</Type>\n" +
                "                <DateOrTimestamp>2016-11-17T00:00:00</DateOrTimestamp>\n" +
                "            </DatesOrTimes>\n" +
                "            <DestinationAddress>\n" +
                "                <City>DENVER</City>\n" +
                "                <StateOrProvinceCode>CO</StateOrProvinceCode>\n" +
                "                <CountryCode>US</CountryCode>\n" +
                "                <CountryName>United States</CountryName>\n" +
                "                <Residential>false</Residential>\n" +
                "            </DestinationAddress>\n" +
                "            <DeliveryAttempts>0</DeliveryAttempts>\n" +
                "            <TotalUniqueAddressCountInConsolidation>0</TotalUniqueAddressCountInConsolidation>\n" +
                "            <NotificationEventsAvailable>ON_DELIVERY</NotificationEventsAvailable>\n" +
                "            <NotificationEventsAvailable>ON_EXCEPTION</NotificationEventsAvailable>\n" +
                "            <NotificationEventsAvailable>ON_ESTIMATED_DELIVERY</NotificationEventsAvailable>\n" +
                "            <NotificationEventsAvailable>ON_TENDER</NotificationEventsAvailable>\n" +
                "            <DeliveryOptionEligibilityDetails>\n" +
                "                <Option>INDIRECT_SIGNATURE_RELEASE</Option>\n" +
                "                <Eligibility>INELIGIBLE</Eligibility>\n" +
                "            </DeliveryOptionEligibilityDetails>\n" +
                "            <DeliveryOptionEligibilityDetails>\n" +
                "                <Option>REDIRECT_TO_HOLD_AT_LOCATION</Option>\n" +
                "                <Eligibility>INELIGIBLE</Eligibility>\n" +
                "            </DeliveryOptionEligibilityDetails>\n" +
                "            <DeliveryOptionEligibilityDetails>\n" +
                "                <Option>REROUTE</Option>\n" +
                "                <Eligibility>INELIGIBLE</Eligibility>\n" +
                "            </DeliveryOptionEligibilityDetails>\n" +
                "            <DeliveryOptionEligibilityDetails>\n" +
                "                <Option>RESCHEDULE</Option>\n" +
                "                <Eligibility>INELIGIBLE</Eligibility>\n" +
                "            </DeliveryOptionEligibilityDetails>\n" +
                "            <Events>\n" +
                "                <Timestamp>2016-11-17T03:13:01-06:00</Timestamp>\n" +
                "                <EventType>OC</EventType>\n" +
                "                <EventDescription>Shipment information sent to FedEx</EventDescription>\n" +
                "                <Address>\n" +
                "                    <Residential>false</Residential>\n" +
                "                </Address>\n" +
                "                <ArrivalLocation>CUSTOMER</ArrivalLocation>\n" +
                "            </Events>\n" +
                "        </TrackDetails>\n" +
                "    </CompletedTrackDetails>\n" +
                "</TrackReply>\n";

        Serializer serializer = new Persister();
        TrackReply trackReply = serializer.read(TrackReply.class, reply, false);

        System.out.println("");
    }
}
