package it.bancomat.pay.consumer.events;

public class HomeCarouselAnimationEvent {

	public enum EAnimationEvent {
		START, STOP
	}

	public enum EAnimationPage {
		P2B_QR, P2P_PAYMENT, P2B_PAYMENT, P2B_ECOMMERCE, B2P_CASHBACK
	}

	private EAnimationEvent event;
	private EAnimationPage page;

	public HomeCarouselAnimationEvent(EAnimationEvent event, EAnimationPage page) {
		this.event = event;
		this.page = page;
	}

	public EAnimationEvent getEvent() {
		return event;
	}

	public void setEvent(EAnimationEvent event) {
		this.event = event;
	}

	public EAnimationPage getPage() {
		return page;
	}

	public void setPage(EAnimationPage page) {
		this.page = page;
	}

}
