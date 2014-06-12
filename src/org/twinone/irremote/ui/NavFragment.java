package org.twinone.irremote.ui;

import java.util.List;

import org.twinone.androidlib.NavigationFragment;
import org.twinone.androidlib.ShareManager;
import org.twinone.androidlib.ShareRateView;
import org.twinone.irremote.R;
import org.twinone.irremote.Remote;
import org.twinone.irremote.ui.SelectRemoteListView.OnSelectListener;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class NavFragment extends NavigationFragment implements
		OnSelectListener, OnClickListener {

	// private static final String PREF_FILENAME = "nav";
	// Keep track of the user's last selected remote
	private static final String PREF_KEY_LAST_REMOTE = "org.twinone.irremote.pref.key.save_remote_name";

	private DrawerLayout mDrawerLayout;
	private SelectRemoteListView mRemotesListView;
	private LinearLayout mRateLayout;
	private LinearLayout mShareLayout;
	private View mFragmentContainerView;

	public NavFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public void update() {
		mRemotesListView.updateRemotesList();

		// select the appropriate remote
		List<String> names = Remote.getNames(getActivity());
		String lastSelectedRemotePref = getPreferences().getString(
				PREF_KEY_LAST_REMOTE, null);
		if (names.contains(lastSelectedRemotePref)) {
			mRemotesListView.selectRemote(lastSelectedRemotePref, false);
		} else if (names.size() > 0) {
			mRemotesListView.selectRemote(0, false);
		} else {
			mRemotesListView.selectRemote(-1, false);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(getActivity() instanceof OnSelectListener)) {
			throw new ClassCastException(
					"Activity should implement SelectRemoteListView.OnSelectListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout root = (RelativeLayout) inflater.inflate(
				R.layout.fragment_nav, container, false);

		ShareRateView srv = (ShareRateView) root
				.findViewById(R.id.share_rate_view);
		srv.addItem(R.id.nav_b_share, R.string.nav_b_share,
				R.drawable.ic_action_share, this);
		srv.addItem(R.id.nav_b_rate, R.string.nav_b_rate,
				R.drawable.ic_action_important, this);

		mRemotesListView = (SelectRemoteListView) root
				.findViewById(R.id.select_remote_listview);
		mRemotesListView.setShowAddRemote(true);
		mRemotesListView.setOnSelectListener(this);

		mRateLayout = (LinearLayout) root.findViewById(R.id.nav_b_rate);
		mShareLayout = (LinearLayout) root.findViewById(R.id.nav_b_share);

		mRateLayout.setOnClickListener(this);
		mShareLayout.setOnClickListener(this);

		update();

		return root;
	}

	/**
	 * This will select the remote in the list, it will also make a call to the
	 * listener
	 * 
	 * @param position
	 */
	public void select(int position) {
		mRemotesListView.selectRemote(position, true);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	public String getSelectedRemoteName() {
		return mRemotesListView.getSelectedRemoteName();
	}

	public int getSelectedRemotePosition() {
		return mRemotesListView.getSelectedItemPosition();
	}

	// -1 when none selected
	private int mTargetRemotePosition = -1;

	@Override
	public void onRemoteSelected(int position, String remoteName) {
		close();

		getPreferences().edit().putString(PREF_KEY_LAST_REMOTE, remoteName)
				.apply();
		mTargetRemotePosition = position;
	}

	@Override
	public void onAddRemoteSelected() {
		((OnSelectListener) getActivity()).onAddRemoteSelected();
	}

	@Override
	protected void onOpen() {
	}

	@Override
	protected void onClose() {
		// We should provide navigation after the drawer has been closed,
		// because of animations
		int pos = mTargetRemotePosition;
		if (pos != -1) {
			((OnSelectListener) getActivity()).onRemoteSelected(pos,
					mRemotesListView.getRemoteName(pos));
			pos = -1;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nav_b_rate:
			onRateButton();
			break;
		case R.id.nav_b_share:
			onShareButton();
			break;
		}
	}

	public void onShareButton() {
		// Don't add never button, the user wanted to share
		// Dialogs.getShareEditDialog(this, false).show();
		ShareManager.getShareEditDialog(getActivity(),
				getActivity().getString(R.string.share_promo), false).show();
	}

	public void onRateButton() {
		toGooglePlay();
	}

	private void toGooglePlay() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id="
				+ getActivity().getPackageName()));
		if (getActivity()
				.getPackageManager()
				.queryIntentActivities(intent,
						PackageManager.MATCH_DEFAULT_ONLY).size() >= 1) {
			startActivity(intent);
		}
	}

}
