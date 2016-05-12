package actions;

import form.SertifikatForm;
import security.CertificateGenerator;
import security.IssuerData;
import security.KeyStoreWriter;
import security.SubjectData;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;

public class CommitAction extends AbstractAction{

private JDialog standardForm;
	
	public CommitAction(JDialog standardForm) {
		KeyStroke ctrlDKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK);
		putValue(ACCELERATOR_KEY,ctrlDKeyStroke);
		putValue(SHORT_DESCRIPTION, "Commit");
		putValue(NAME, "Commit");
		
		this.standardForm=standardForm;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {	
		
		if(standardForm instanceof SertifikatForm)
		{
			SertifikatForm sertForm = (SertifikatForm)standardForm;
			//C
			String c = sertForm.getC().getText();
			//L
			String l = sertForm.getL().getText();
			//O
			String o = sertForm.getOn().getText();
			//OU		
			String ou = sertForm.getOu().getText();
			//CN	
			String cn = sertForm.getCn().getText();
			//broj dana
			int brojDana =Integer.parseInt(sertForm.getDays().getText());
			SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
			
			Calendar today = Calendar.getInstance();
			today.setTime(new Date());
			Date startDate = today.getTime();
			today.add(Calendar.DATE, brojDana);
			
			Date endDate = today.getTime();
			System.out.println(startDate);
			System.out.println(endDate);
			X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
			
			
		    builder.addRDN(BCStyle.CN, cn);
		   // builder.addRDN(BCStyle.SURNAME, prz);
		  //  builder.addRDN(BCStyle.GIVENNAME, ime);
		    builder.addRDN(BCStyle.O, o);
		    builder.addRDN(BCStyle.OU, ou);
		    builder.addRDN(BCStyle.C, c);
		    //builder.addRDN(BCStyle.E, "sladicg@uns.ac.rs");
		    //UID (USER ID) je ID korisnika
		    //ajde da ovo za pocetak ne bude promenljivo...
		    builder.addRDN(BCStyle.UID, "123445");
		    KeyPair keyPair = CertificateGenerator.generateKeyPair();
		    //Serijski broj sertifikata
		    //ovo bi trebalo da se inkrementira, a?
			String sn="1";
		    SubjectData subjData = new SubjectData(keyPair.getPublic(),builder.build(),sn,startDate,endDate);
		    X509Certificate cert = null;
		    if(sertForm.getIzdavalac().getSelectedItem().equals("samopotpisan"))
		    {
		    	IssuerData issuerData = new IssuerData(keyPair.getPrivate(),builder.build());
		    	cert = CertificateGenerator.generateCertificate( issuerData,subjData);
		    	System.out.println("fggfgf");
		    }
		    KeyStoreWriter ksw = new KeyStoreWriter();
		    
		    File f = new File("./data/sgns.jks");
		    if(!f.exists() || f.isDirectory()) { 
		    	ksw.loadKeyStore(null,"sgns".toCharArray());
		    }
		    else ksw.loadKeyStore("./data/sgns.jks","sgns".toCharArray());
		    
		    ksw.write(sertForm.getAlias().getText(), keyPair.getPrivate(), "test10".toCharArray(), cert);
			ksw.saveKeyStore("./data/sgns.jks", "sgns".toCharArray());
		   
		}
		standardForm.dispose();
	}
}