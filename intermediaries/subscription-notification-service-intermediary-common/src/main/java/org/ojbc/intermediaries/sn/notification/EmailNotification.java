package org.ojbc.intermediaries.sn.notification;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The class of objects representing email-based notifications.  This class is used to communicate between processors and through steps in the
 * Camel route.
 *
 */
public final class EmailNotification {
    
    private static final Log log = LogFactory.getLog(EmailNotification.class);

    private String subjectName;
    private String subscribingSystemIdentifier;
    private String subjectIdentifier;
    private Set<String> toAddressees = new HashSet<String>();
    private Set<String> ccAddressees = new HashSet<String>();
    private Set<String> bccAddressees = new HashSet<String>();
    private Set<String> blockedAddressees = new HashSet<String>();
    private NotificationRequest notificationRequest;
    
    public EmailNotification() {
        super();
    }

    public NotificationRequest getNotificationRequest() {
        return notificationRequest;
    }

    public void setNotificationRequest(NotificationRequest notificationRequest) {
        this.notificationRequest = notificationRequest;
        setSubjectIdentifier(notificationRequest.getDescriptiveSubjectIdentifier());
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubscribingSystemIdentifier() {
        return subscribingSystemIdentifier;
    }

    public void setSubscribingSystemIdentifier(String subscribingSystemIdentifier) {
        this.subscribingSystemIdentifier = subscribingSystemIdentifier;
    }
    
    public Set<String> getToAddresseeSet() {
        return Collections.unmodifiableSet(toAddressees);
    }

    public Set<String> getCcAddresseeSet() {
        return Collections.unmodifiableSet(ccAddressees);
    }

    public Set<String> getBlockedAddresseeSet() {
        return Collections.unmodifiableSet(blockedAddressees);
    }
    
    public void applyAddressWhitelist(Set<String> whitelist) {
        Set<String> newTo = new HashSet<String>();
        applyWhitelistToSet(whitelist, toAddressees, newTo);
        toAddressees = newTo;
    }

    private void applyWhitelistToSet(Set<String> whitelist, Set<String> currentSet, Set<String> newSet) {
        for (String s : currentSet) {
            if (whitelist.contains(s)) {
                log.debug("Email address " + s + " is in the whitelist");
                newSet.add(s);
            } else {
                log.debug("Email address " + s + " is not in the whitelist, adding to blocked list");
                blockedAddressees.add(s);
            }
        }
    }

    public Set<String> getBccAddresseeSet() {
        return Collections.unmodifiableSet(bccAddressees);
    }

    public String getToAddressees() {
        return createCommaSeparatedStringFromSet(toAddressees);
    }

    public void addToAddressee(String toAddressee) {
        toAddressees.add(toAddressee);
    }

    public String getCcAddressees() {
        return createCommaSeparatedStringFromSet(ccAddressees);
    }

    public void addCcAddressee(String ccAddressee) {
        ccAddressees.add(ccAddressee);
    }

    public String getBccAddressees() {
        return createCommaSeparatedStringFromSet(bccAddressees);
    }

    public void addBccAddressee(String bccAddressee) {
        bccAddressees.add(bccAddressee);
    }

    private String createCommaSeparatedStringFromSet(Set<String> addresses) {
        if (addresses.isEmpty()) {
            return null;
        }
        StringBuffer ret = new StringBuffer();
        for (String address : addresses) {
            ret.append(address).append(",");
        }
        if (ret.length() != 0) {
            ret.setLength(ret.length()-1);
        }
        return ret.toString();
    }
    
    @Override
    public boolean equals(Object comp) {
        boolean ret = false;
        if (comp instanceof EmailNotification) {
            EmailNotification n = (EmailNotification) comp;
            ret = n.subjectName.equals(subjectName) && n.subscribingSystemIdentifier.equals(subscribingSystemIdentifier) && n.toAddressees.equals(toAddressees) && n.ccAddressees.equals(ccAddressees) && n.bccAddressees.equals(bccAddressees);
        }
        return ret;
    }
    
    @Override
    public Object clone() {
        EmailNotification copy = new EmailNotification();
        copy.subjectName = subjectName;
        copy.subscribingSystemIdentifier = subscribingSystemIdentifier;
        copy.subjectIdentifier = subjectIdentifier;
        copy.bccAddressees.addAll(bccAddressees);
        copy.ccAddressees.addAll(ccAddressees);
        copy.toAddressees.addAll(toAddressees);
        copy.blockedAddressees.addAll(blockedAddressees);
        copy.notificationRequest = notificationRequest;
        return copy;
    }

    private void setSubjectIdentifier(String subjectIdentifier) {
        this.subjectIdentifier = subjectIdentifier;
    }
    
    public String getSubjectIdentifier() {
        return subjectIdentifier;
    }

	@Override
	public String toString() {
		return "EmailNotification [subjectName=" + subjectName
				+ ", subscribingSystemName=" + subscribingSystemIdentifier
				+ ", subjectIdentifier=" + subjectIdentifier
				+ ", toAddressees=" + toAddressees + ", ccAddressees="
				+ ccAddressees + ", bccAddressees=" + bccAddressees
				+ ", blockedAddressees=" + blockedAddressees + "]";
	}

}