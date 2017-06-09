package mn.mobicom.oauth2;

public interface OAuthCallback<T> {

	public void onAuthFailure(OAuthException ex);

	public void onSuccess(T result);

	public boolean onStarted();

	public void onFailure(Throwable th);

	public void onSuccess(String json);
}
