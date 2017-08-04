package mn.mobicom.classes;

import android.content.Context;
import android.view.View;

import org.linphone.LinphoneLauncherActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.R;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneProxyConfig;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by showtime on 8/4/17.
 */
public class CheckState {

    public interface StateCreateOrUse {
        void action();
    }


    public static void checkState(Context context, StateCreateOrUse stateCreateOrUse) {
        if (LinphoneLauncherActivity.STATE.equals("create") || LinphoneLauncherActivity.STATE.equals("use")){
            if (stateCreateOrUse != null) {
                stateCreateOrUse.action();
            }
        }else if (LinphoneLauncherActivity.STATE.equals("expire14")){
            final MaterialDialog mMaterialDialog = new MaterialDialog(
                    context);
            mMaterialDialog
                    .setMessage(context.getResources().getString(R.string.mnp_warning_expire14))
                    .setPositiveButton("OK",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                }
                            });

            mMaterialDialog.show();
        }else if (LinphoneLauncherActivity.STATE.equals("expire90")){
            final MaterialDialog mMaterialDialog = new MaterialDialog(
                    context);
            mMaterialDialog
                    .setMessage(context.getResources().getString(R.string.mnp_warning_expire90))
                    .setPositiveButton("OK",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                }
                            });

            mMaterialDialog.show();
        }else if (LinphoneLauncherActivity.STATE.equals("suspend")){
            final MaterialDialog mMaterialDialog = new MaterialDialog(
                    context);
            mMaterialDialog
                    .setMessage(context.getResources().getString(R.string.mnp_warning_suspend))
                    .setPositiveButton("OK",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                }
                            });

            mMaterialDialog.show();
        }
    }
}
