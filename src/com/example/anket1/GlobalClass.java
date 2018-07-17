package com.example.anket1;

import android.app.Application;

public class GlobalClass extends Application {

	String sorumuz = "Hizmetlerinden Memnun Kaldınız mı?";
	String soruID;
	String name;
	String kG_Banner;
	String bitis_banner;

	public void setKullaniciGirisiBanner(String kG_Banner) {

		this.kG_Banner = kG_Banner;
	}

	public String getKullaniciGirisiBanner() {

		return kG_Banner;
	}

	public void setBannerName(String name) {

		this.name = name;
	}

	public String getBannerName() {

		return name;
	}

	public String getSorumuz() {

		return sorumuz;
	}

	public void setSorumuz(String soru) {

		sorumuz = soru;

	}

	public String getSoruID() {

		return sorumuz;
	}

	public void setSoruID(String ID) {

		soruID = ID;

	}

	public void setBitisBanner(String bit_ban) {

		bitis_banner = bit_ban;
	}

	public String getBitisBanner() {

		return bitis_banner;
	}

}
