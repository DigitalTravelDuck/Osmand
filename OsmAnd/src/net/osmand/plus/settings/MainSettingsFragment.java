package net.osmand.plus.settings;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.view.View;

import net.osmand.AndroidUtils;
import net.osmand.plus.ApplicationMode;
import net.osmand.plus.R;
import net.osmand.plus.UiUtilities;
import net.osmand.plus.profiles.SettingsProfileFragment;

public class MainSettingsFragment extends BaseSettingsFragment {

	public static final String TAG = MainSettingsFragment.class.getSimpleName();

	private static final String CONFIGURE_PROFILE = "configure_profile";
	private static final String MANAGE_PROFILES = "manage_profiles";

	@Override
	protected String getFragmentTag() {
		return TAG;
	}

	@Override
	protected int getPreferencesResId() {
		return R.xml.settings_main_screen;
	}

	@Override
	protected int getToolbarResId() {
		return R.layout.global_preference_toolbar;
	}

	@Override
	protected int getToolbarTitle() {
		return R.string.shared_string_settings;
	}

	@Override
	public int getStatusBarColorId() {
		return isNightMode() ? R.color.status_bar_color_dark : R.color.status_bar_color_light;
	}

	@ColorRes
	protected int getBackgroundColorRes() {
		return isNightMode() ? R.color.activity_background_color_dark : R.color.activity_background_color_light;
	}

	@Override
	protected void setupPreferences() {
		Preference globalSettings = findPreference("global_settings");
		globalSettings.setIcon(getContentIcon(R.drawable.ic_action_settings));

		setupConfigureProfilePref();
		setupManageProfilesPref();
	}

	@Override
	protected void onBindPreferenceViewHolder(Preference preference, PreferenceViewHolder holder) {
		super.onBindPreferenceViewHolder(preference, holder);

		String key = preference.getKey();
		if (CONFIGURE_PROFILE.equals(key)) {
			View iconContainer = holder.itemView.findViewById(R.id.icon_container);
			if (iconContainer != null) {
				int profileColor = getActiveProfileColor();
				int bgColor = UiUtilities.getColorWithAlpha(profileColor, 0.1f);
				Drawable backgroundDrawable;

				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
					int selectedColor = UiUtilities.getColorWithAlpha(profileColor, 0.3f);
					Drawable background = getPaintedIcon(R.drawable.circle_background_light, bgColor);
					Drawable ripple = getPaintedIcon(R.drawable.ripple_circle, selectedColor);
					backgroundDrawable = new LayerDrawable(new Drawable[] {background, ripple});
				} else {
					backgroundDrawable = getPaintedIcon(R.drawable.circle_background_light, bgColor);
				}
				AndroidUtils.setBackground(iconContainer, backgroundDrawable);
			}
		}
	}

	private void setupManageProfilesPref() {
		Preference manageProfiles = findPreference(MANAGE_PROFILES);
		manageProfiles.setIcon(getIcon(R.drawable.ic_action_manage_profiles));
	}

	private void setupConfigureProfilePref() {
		ApplicationMode selectedMode = getSelectedAppMode();

		String title = selectedMode.toHumanString(getContext());
		String profileType = getAppModeDescription(getContext(), selectedMode);
		int iconRes = selectedMode.getIconRes();

		Preference configureProfile = findPreference(CONFIGURE_PROFILE);
		configureProfile.setIcon(getPaintedIcon(iconRes, getActiveProfileColor()));
		configureProfile.setTitle(title);
		configureProfile.setSummary(profileType);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (MANAGE_PROFILES.equals(preference.getKey())) {
			FragmentActivity activity = getActivity();
			if (activity != null) {
				FragmentManager fragmentManager = activity.getSupportFragmentManager();
				if (fragmentManager != null) {
					SettingsProfileFragment.showInstance(fragmentManager);
					return true;
				}
			}
		}

		return super.onPreferenceClick(preference);
	}

	public static boolean showInstance(FragmentManager fragmentManager) {
		try {
			MainSettingsFragment MainSettingsFragment = new MainSettingsFragment();
			fragmentManager.beginTransaction()
					.replace(R.id.fragmentContainer, MainSettingsFragment, TAG)
					.addToBackStack(TAG)
					.commitAllowingStateLoss();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}