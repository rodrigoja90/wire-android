/**
 * Wire
 * Copyright (C) 2017 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.waz.zclient.appentry.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.View.OnClickListener
import android.view.{LayoutInflater, View, ViewGroup}
import com.waz.ZLog
import com.waz.zclient.appentry.controllers.SignInController
import com.waz.zclient.appentry.fragments.AddEmailFragment._
import com.waz.zclient.newreg.views.PhoneConfirmationButton
import com.waz.zclient.newreg.views.PhoneConfirmationButton.State.{CONFIRM, NONE}
import com.waz.zclient.pages.BaseFragment
import com.waz.zclient.pages.main.profile.views.GuidedEditText
import com.waz.zclient.utils._
import com.waz.zclient.{FragmentHelper, R}
import com.waz.ZLog.ImplicitTag._
import com.waz.threading.Threading
import com.waz.zclient.appentry.EntryError
import com.waz.zclient.appentry.controllers.SignInController.{Email, Register, SignInMethod}

class AddEmailFragment extends BaseFragment[Container] with FragmentHelper with OnClickListener {

  lazy val signInController = inject[SignInController]

  lazy val confirmationButton = Option(findById[PhoneConfirmationButton](R.id.confirmation_button))

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View =
    inflater.inflate(R.layout.add_email_fragment, container, false)

  override def onViewCreated(view: View, savedInstanceState: Bundle) = {
    signInController.uiSignInState ! SignInMethod(Register, Email)

    Option(findById[GuidedEditText](getView, R.id.email_field)).foreach { field =>
      field.setValidator(signInController.emailValidator)
      field.setResource(R.layout.guided_edit_text_sign_in__email)
      field.getEditText.addTextListener(signInController.email ! _)
    }

    Option(findById[GuidedEditText](getView, R.id.password_field)).foreach { field =>
      field.setValidator(signInController.passwordValidator)
      field.setResource(R.layout.guided_edit_text_sign_in__password)
      field.setText(signInController.password.currentValue.getOrElse(""))
      field.getEditText.addTextListener(signInController.password ! _)
    }

    confirmationButton.foreach { button =>
      button.setOnClickListener(this)
      button.setAccentColor(Color.WHITE)
    }

    setConfirmationButtonActive(signInController.isValid.currentValue.getOrElse(false))
    signInController.isAddEmailValid.onUi { setConfirmationButtonActive }
    Option(findById[View](R.id.cancel_button)).foreach(_.setOnClickListener(this))
    Option(findById[View](R.id.ttv_signin_forgot_password)).foreach(_.setOnClickListener(this))
  }

  private def setConfirmationButtonActive(active: Boolean): Unit =
    confirmationButton.foreach(_.setState(if(active) CONFIRM else NONE))

  override def onClick(v: View): Unit = {
    getContainer.enableProgress(true)
    signInController.addEmail().map {
      case Left(error) =>
        getContainer.enableProgress(false)
        getContainer.showError(error)
      case _ =>
    } (Threading.Ui)
  }
}

object AddEmailFragment {
  val Tag = ZLog.ImplicitTag.implicitLogTag
  trait Container {
    def enableProgress(enabled: Boolean): Unit
    def showError(entryError: EntryError, okCallback: => Unit = {}): Unit
  }
}
