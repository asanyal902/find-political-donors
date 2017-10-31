import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class FindPoliticalDonors {

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Expected Usage: Input Filename, Output File1, Output File2");
			return;
		}
		String inputFile = args[0];
		String zipOutFile = args[1];
		String dateOutFile = args[2];

		File file = new File(inputFile);
		BufferedReader bufferedReader = null;
		PrintWriter zipWriter = null, dateWriter = null;
		
		TreeMap<DateKey, MedianGetter> dateCache = new TreeMap<>();
		HashMap<ZipKey, MedianGetter> zipCache = new HashMap<>();

		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			zipWriter = new PrintWriter(zipOutFile, "UTF-8");
			dateWriter = new PrintWriter(dateOutFile, "UTF-8");
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				String[] raw = line.split("\\|");
				if(raw == null || raw.length < 16) {
					continue;
				}
				
				//pojofy the raw data
				FecData data = new FecData(raw);
				
				if(data.isValidForZip()){
					//add to the zip cache
					ZipKey zKey = new ZipKey(data.zipcode, data.cmteId);
					if(!zipCache.containsKey(zKey)){
						zipCache.put(zKey, new MedianGetter());
					}
					MedianGetter med = zipCache.get(zKey);
					med.addDonation(data.tranAmount);
					//write the zip output
					StringBuilder s = new StringBuilder();
					s.append(zKey.cmteId).append("|")
					.append(zKey.zip).append("|")
					.append(med.getMedian().toPlainString()).append("|")
					.append(med.getTotalTrans()).append("|")
					.append(med.getTotalContrib().toPlainString());
					zipWriter.println(s.toString());
				}
				
				if(data.isValidForDate()){
					//add to the date cache
					DateKey dKey = new DateKey(data.tranDate, data.cmteId);
					if(!dateCache.containsKey(dKey)){
						dateCache.put(dKey, new MedianGetter());
					}
					dateCache.get(dKey).addDonation(data.tranAmount);
				}
			}
			
			//loop through date cache and write each combination.
			for(DateKey key: dateCache.keySet()){
				StringBuilder s = new StringBuilder();
				MedianGetter med = dateCache.get(key);
				s.append(key.cmteId).append("|")
				.append(new SimpleDateFormat("MMddyyyy").format(key.date)).append("|")
				.append(med.getMedian().toPlainString()).append("|")
				.append(med.getTotalTrans()).append("|")
				.append(med.getTotalContrib().toPlainString());
				dateWriter.println(s.toString());
			}
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null)
					bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (zipWriter != null) zipWriter.close();
			if (dateWriter != null) dateWriter.close();
		}
	}

	private static class DateKey implements Comparable<DateKey> {
		Date date;
		String cmteId;

		DateKey(Date d, String cmteId) {
			this.date = d;
			this.cmteId = cmteId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((cmteId == null) ? 0 : cmteId.hashCode());
			result = prime * result + ((date == null) ? 0 : date.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DateKey other = (DateKey) obj;
			if (cmteId == null) {
				if (other.cmteId != null)
					return false;
			} else if (!cmteId.equals(other.cmteId))
				return false;
			if (date == null) {
				if (other.date != null)
					return false;
			} else if (!date.equals(other.date))
				return false;
			return true;
		}

		@Override
		public int compareTo(DateKey other) {
			if(this == other) return 0;
			if(other == null) return 1;
			//sort by recepient, then date.
			if(this.cmteId.equals(other.cmteId)) return this.date.compareTo(other.date);
			return this.cmteId.compareTo(other.cmteId);
		}
	}

	private static class ZipKey {
		String zip;
		String cmteId;

		ZipKey(String zip, String cmteId) {
			this.zip = zip;
			this.cmteId = cmteId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((cmteId == null) ? 0 : cmteId.hashCode());
			result = prime * result + ((zip == null) ? 0 : zip.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ZipKey other = (ZipKey) obj;
			if (cmteId == null) {
				if (other.cmteId != null)
					return false;
			} else if (!cmteId.equals(other.cmteId))
				return false;
			if (zip == null) {
				if (other.zip != null)
					return false;
			} else if (!zip.equals(other.zip))
				return false;
			return true;
		}
	}

}
