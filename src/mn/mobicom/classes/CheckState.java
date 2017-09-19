package mn.mobicom.classes;

import android.content.Context;
import android.view.View;

import mn.mobinet.mnp75.LinphoneLauncherActivity;

import mn.mobinet.mnp75.R;

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
