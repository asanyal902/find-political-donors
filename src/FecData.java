import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*Pojo file for holding FEC data we need*/
public class FecData {
	/*
	 *CMTE_ID: identifies the flier, which for our purposes is the 
	 *recipient of this contribution. Pos: 1
	 *ZIP_CODE: zip code of the contributor (we only want the first five digits/characters). Pos:11
	 *TRANSACTION_DT: date of the transaction. Pos:14
	 *TRANSACTION_AMT: amount of the transaction Pos:15
	 *OTHER_ID: a field that denotes whether contribution came from a person or an entity. Pos:16
	 * */
	
	public String cmteId, zipcode,otherId;
	public Date tranDate;
	public BigDecimal tranAmount;
	
	
	public FecData(String[] raw){
		this.build(raw);
	}
	
	private void build(String[] raw) {
		cmteId = raw[0].trim();
		zipcode = (raw[10] != null && raw[10].trim().length() >= 5) ? raw[10].trim().substring(0, 5) : null;
		DateFormat format = new SimpleDateFormat("MMddyyyy", Locale.ENGLISH);
		try {
			if (!isEmpty(raw[13]) && isValidDate(raw[13].trim())){
				tranDate = format.parse(raw[13].trim());
			}
		} catch (ParseException e) {
			//date not in the right format
			e.printStackTrace();
		}
		
		try{
			if(!isEmpty(raw[14])){
				tranAmount = new BigDecimal(raw[14].trim());
			}

		} catch(NumberFormatException e){
			//amnt not in the right format
			e.printStackTrace();
		}
		
		otherId = !isEmpty(raw[15]) ? raw[15].trim() : null;
	}

	public boolean isValidForZip() {
		return this.isValid() && !isEmpty(zipcode);
	}

	public boolean isValidForDate() {
		return this.isValid() && tranDate != null;
	}
	
	//conditions for both zip and date
	private boolean isValid(){
		return isEmpty(otherId) && !isEmpty(cmteId) && tranAmount != null;
	}

	//check date is in a valid MMddyyyy format with 1 <= MM <= 12 & 1 <= dd <= 31
	private boolean isValidDate(String date){
		if(date.length() != 8) return false;
		try{
			int month = Integer.parseInt(date.substring(0,2));
			int day = Integer.parseInt(date.substring(2,4));
			return (month >= 1  && month <= 12 && day >= 1 && day <= 31);
		}
		catch(NumberFormatException e){
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean isEmpty(String s){
		return s == null || s.trim().length() == 0;
	}


}
