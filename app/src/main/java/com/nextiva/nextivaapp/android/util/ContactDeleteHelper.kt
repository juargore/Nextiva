package com.nextiva.nextivaapp.android.util

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetDeleteConfirmation
import com.nextiva.nextivaapp.android.fragments.bottomsheets.BottomSheetGenericError
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ConversationRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformContactsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.SmsManagementRepository
import com.nextiva.nextivaapp.android.models.NextivaContact
import io.reactivex.disposables.CompositeDisposable

class ContactDeleteHelper(
    val activity: Activity,
    private val fragmentManager: FragmentManager,
    val compositeDisposable: CompositeDisposable,
    val dbManager: DbManager,
    val platformContactsRepository: PlatformContactsRepository,
    val smsManagementRepository: SmsManagementRepository,
    val conversationRepository: ConversationRepository
) {

    fun deleteContactConfirmation(
        contact: NextivaContact?,
        resetCallback: () -> Unit,
        onSuccessCallback: () -> Unit
    ) {
        val contact = contact ?: return
        BottomSheetDeleteConfirmation.newInstance(
            title = activity.resources.getString(R.string.connect_contacts_details_delete_title),
            subtitle = activity.resources.getString(R.string.connect_contacts_details_delete_message),
            deleteAction = {
                deleteContact(
                    contact = contact,
                    resetCallback = resetCallback,
                    onSuccessCallback = {
                        activity.runOnUiThread {
                            showCustomToast()
                            onSuccessCallback()
                        }
                    }
                )
            },
            cancelAction = {}
        ).show(fragmentManager, null)
    }

    private fun showCustomToast() {
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_toast, null)
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = view
        toast.show()
    }

    private fun deleteContact(
        contact: NextivaContact,
        resetCallback: () -> Unit,
        onSuccessCallback: () -> Unit
    ) {
        compositeDisposable.add(
            platformContactsRepository.deleteContact(contact).subscribe { isSuccess ->
                if (isSuccess) {
                    dbManager.deleteContactByContactId(compositeDisposable, contact.userId)
                    resetCallback()
                    onSuccessCallback()

                    // refresh sms history and call history so the deleted
                    // contact displays properly as phone number instead of name
                    smsManagementRepository.getSmsConversations()
                        .subscribe()
                    conversationRepository.fetchVoiceConversationMessages()
                        .subscribe()
                } else {
                    deleteFailed(contact, resetCallback, onSuccessCallback)
                }
            }
        )
    }

    private fun deleteFailed(
        contact: NextivaContact,
        resetCallback: () -> Unit,
        onSuccessCallback: () -> Unit
    ) {
        BottomSheetGenericError(
            activity.resources.getString(R.string.connect_contacts_delete_error_title),
            activity.resources.getString(R.string.connect_contacts_delete_error_message),
            activity.resources.getString(R.string.connect_contacts_delete_error_retry)
        ) {
            deleteContact(
                contact = contact,
                resetCallback = resetCallback,
                onSuccessCallback = {
                    activity.runOnUiThread {
                        showCustomToast()
                        onSuccessCallback()
                    }
                }
            )
        }.show(fragmentManager, null)
    }
}
