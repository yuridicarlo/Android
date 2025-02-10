package it.bancomatpay.sdk.manager.events.update;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.manager.task.model.UserData;

public class UserDataUpdate {

	private Result<UserData> homePageDetailsResult;

	public UserDataUpdate(Result<UserData> homePageDetailsDataResult) {
		this.homePageDetailsResult = homePageDetailsDataResult;
	}

	public Result<UserData> getUserDataResult() {
		return homePageDetailsResult;
	}

}
