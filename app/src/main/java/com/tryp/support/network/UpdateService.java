package com.tryp.support.network;

import android.util.Log;

import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Created by cliffroot on 11.03.16.
 */
public class UpdateService extends GcmTaskService {

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.e("UPDATE", "updateService.onRunTask -> invoked");
        return 0;
    }
}
