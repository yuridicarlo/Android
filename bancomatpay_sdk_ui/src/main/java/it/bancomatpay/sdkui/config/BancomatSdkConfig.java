package it.bancomatpay.sdkui.config;

public class BancomatSdkConfig {

	public static final String SDK_CONFIG_JSON_FILE_NAME = "bancomat_sdk_config.json";

	private GenericFlags genericFlags;
	private AndroidFlags androidFlags;

	public BancomatSdkConfig() {
		this.genericFlags = new GenericFlags();
		this.androidFlags = new AndroidFlags();
	}

	public GenericFlags getGenericFlags() {
		return genericFlags;
	}

	public AndroidFlags getAndroidFlags() {
		return androidFlags;
	}

}
