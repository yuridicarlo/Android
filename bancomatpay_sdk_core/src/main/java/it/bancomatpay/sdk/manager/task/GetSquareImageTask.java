package it.bancomatpay.sdk.manager.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import it.bancomatpay.sdk.Result;
import it.bancomatpay.sdk.core.OnNetworkCompleteListener;
import it.bancomatpay.sdk.manager.network.dto.DtoStatus;
import it.bancomatpay.sdk.manager.network.dto.response.DtoAppResponse;
import it.bancomatpay.sdk.manager.task.model.CameraImageProcessingResult;
import it.bancomatpay.sdk.manager.task.model.CameraPicture;
import it.bancomatpay.sdk.manager.utilities.CustomLogger;
import it.bancomatpay.sdk.manager.utilities.statuscode.StatusCode;

public class GetSquareImageTask extends ExtendedTask<CameraImageProcessingResult> {

	private static final String TAG = GetSquareImageTask.class.getSimpleName();

	private byte[] imageData;
	private String fileName;

	public GetSquareImageTask(OnCompleteResultListener<CameraImageProcessingResult> mListener, byte[] imageData, String fileName) {
		super(mListener);
		this.imageData = imageData;
		this.fileName = fileName;
	}

	@Override
	protected void start() {

		CameraPicture cameraPicture = new CameraPicture();
		cameraPicture.setInitialData(imageData);
		cameraPicture.setFileName(fileName);

		new TaskCreateCroppedImage()
				.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, cameraPicture);
	}

	private OnNetworkCompleteListener<DtoAppResponse<CameraImageProcessingResult>> listener = new NetworkListener<CameraImageProcessingResult>(this) {

		@Override
		protected void manageComplete(DtoAppResponse<CameraImageProcessingResult> response) {
			CustomLogger.d(TAG, response.toString());
			Result<CameraImageProcessingResult> r = new Result<>();
			prepareResult(r, response);
			if (response.getRes() != null) {
				r.setResult(response.getRes());
			}
			sendCompletition(r);
		}

	};

	private class TaskCreateCroppedImage extends AsyncTask<CameraPicture, Void, CameraImageProcessingResult> {

		@Override
		protected CameraImageProcessingResult doInBackground(CameraPicture... param) {

			Matrix rotationMatrix = new Matrix();
			rotationMatrix.postRotate(90); // for orientation

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = false;
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			opts.inDither = true;
			opts.inSampleSize = 2;

			Bitmap imageOriginal = BitmapFactory.decodeByteArray(param[0].getInitialData(), 0, param[0].getInitialData().length, opts);

			if (imageOriginal.getWidth() < imageOriginal.getHeight()) {
				rotationMatrix = null;
			}
			Bitmap rotatedBitmap = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), rotationMatrix, true);

			int width = rotatedBitmap.getWidth();
			int height = rotatedBitmap.getHeight();
			/*int narrowSize = Math.min(width, height);
			int differ = Math.abs((rotatedBitmap.getHeight() - rotatedBitmap.getWidth()));  // for dimension
			width = (width != narrowSize) ? 0 : differ;
			height = (width != 0) ? differ : 0;*/

			int outputWidth = width - (width / 100 * 26);
			int outputHeight = height / 3;

			int initialXAxis = (width - outputWidth) / 2;

			Bitmap imageCropped = Bitmap.createBitmap(rotatedBitmap, initialXAxis, outputHeight, outputWidth, outputHeight, null, true);
			//imageCropped = Bitmap.createScaledBitmap(imageCropped, outputWidth / 2, outputHeight / 2, true);

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			imageCropped.compress(Bitmap.CompressFormat.JPEG, 30, out);
			Bitmap bitmapCompressed = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmapCompressed.compress(Bitmap.CompressFormat.JPEG, 30, stream);
			byte[] bitmapData = stream.toByteArray();
			bitmapCompressed.recycle();

			String base64Image = Base64.encodeToString(bitmapData, Base64.NO_WRAP);

			CameraImageProcessingResult imageResult = new CameraImageProcessingResult();
			imageResult.setBase64Image(base64Image);

			return imageResult;
		}

		@Override
		protected void onPostExecute(CameraImageProcessingResult imageResult) {
			super.onPostExecute(imageResult);

			Result<CameraImageProcessingResult> result = new Result<>();
			result.setResult(imageResult);
			result.setStatusCode(StatusCode.Mobile.OK);
			DtoAppResponse<CameraImageProcessingResult> response = new DtoAppResponse<>();
			DtoStatus dtoStatus = new DtoStatus();
			dtoStatus.setStatusCode("0000");
			response.setDtoStatus(dtoStatus);

			prepareResult(result, response);
			response.setRes(imageResult);

			listener.onComplete(response);
		}

	}

}
