/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */

package github.daneren2005.dsub.service.sync;

import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import github.daneren2005.dsub.domain.MusicDirectory;
import github.daneren2005.dsub.domain.Playlist;
import github.daneren2005.dsub.service.DownloadFile;
import github.daneren2005.dsub.util.Util;

/**
 * Created by Scott on 8/28/13.
 */

public class PodcastSyncAdapter extends SubsonicSyncAdapter {
	public PodcastSyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}
	@TargetApi(14)
	public PodcastSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
	}

	@Override
	public void onExecuteSync(Context context, int instance) {
		String serverName = Util.getServerName(context, instance);
		String podcastListFile = "sync-podcast-" + (Util.getRestUrl(context, null, instance)).hasCode() + ".ser";
		List<Integer> podcastList = FileUtil.deserialize(context, podcastListFile, ArrayList.class);
		for(int i = 0; i < podcastList.size(); i++) {
			String id = Integer.toString(podcastList.get(i));
			List<PodcastChannel> podcasts = musicService.getPodcastEpisodes(true, id, context, null);
			
			// TODO: Only grab most recent podcasts!
			for(PodcastChannel entry: podcasts) {
				DownloadFile file = new DownloadFile(context, entry, true);
				while(!file.isSaved() && !file.isFailedMax()) {
					file.downloadNow();
				}
			}
		}
	}
}
