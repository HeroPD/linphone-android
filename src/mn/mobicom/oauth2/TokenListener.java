package mn.mobicom.oauth2;

public interface TokenListener {

	public void accessToken(String accessToken);

	public void refreshToken(String refreshToken);
}
