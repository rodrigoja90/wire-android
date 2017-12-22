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
package com.waz.zclient.usersearch.adapters

import android.support.v7.widget.RecyclerView
import android.view.{LayoutInflater, View, ViewGroup}
import com.waz.ZLog
import com.waz.ZLog.ImplicitTag._
import com.waz.api.{Contact, ContactDetails, User}
import com.waz.model._
import com.waz.threading.Threading
import com.waz.utils.events.EventContext
import com.waz.zclient._
import com.waz.zclient.common.controllers.{SearchUserController, UserAccountsController}
import com.waz.zclient.usersearch.SearchResultOnItemTouchListener
import com.waz.zclient.usersearch.viewholders._
import com.waz.zclient.usersearch.views.ContactRowView
import PickUsersAdapter._
import scala.concurrent.duration._

class PickUsersAdapter(topUsersOnItemTouchListener: SearchResultOnItemTouchListener,
                       adapterCallback: PickUsersAdapter.Callback,
                       searchUserController: SearchUserController,
                       darkTheme: Boolean)
                      (implicit injector: Injector) extends RecyclerView.Adapter[RecyclerView.ViewHolder] with Injectable {

  implicit private val ec = EventContext.Implicits.global

  setHasStableIds(true)

  private val userAccountsController = inject[UserAccountsController]

  private var mergedResult = Seq[SearchResult]()
  private var collapsedContacts = true
  private var collapsedGroups = true

  private val contactsCallback = new ContactRowView.Callback {
    override def onContactListContactClicked(contactDetails: ContactDetails) = adapterCallback.onContactListContactClicked(contactDetails)
    override def isUserSelected(user: User) = adapterCallback.getSelectedUsers.contains(UserId(user.getId))
    override def onContactListUserClicked(user: User) = adapterCallback.onContactListUserClicked(UserId(user.getId))
  }

  private var topUsers = IndexedSeq.empty[UserData]
  private var localResults = IndexedSeq.empty[UserData]
  private var conversations = IndexedSeq.empty[ConversationData]
  private var contacts = Seq.empty[Contact]
  private var directoryResults = IndexedSeq.empty[UserData]
  private var currentUser = Option.empty[UserData]

  searchUserController.allDataSignal.throttle(500.millis).on(Threading.Ui) {
    case (newTopUsers, newLocalResults, newConversations, newContacts, newDirectoryResults) =>
      newTopUsers.foreach(topUsers = _)
      newLocalResults.foreach(localResults = _)
      newConversations.foreach(conversations = _)
      contacts = newContacts
      newDirectoryResults.foreach(directoryResults = _)
      updateMergedResults()
  }

  userAccountsController.currentUser.on(Threading.Ui){ user =>
    currentUser = user
    updateMergedResults()
  }

  private def updateMergedResults(): Unit = {
    mergedResult = Seq()

    val teamName = userAccountsController.teamData.map(_.name).getOrElse("")

    def addTopPeople(): Unit = {
      if (topUsers.nonEmpty) {
        mergedResult = mergedResult ++ Seq(SearchResult(SectionHeader, TopUsersSection, 0))
        mergedResult = mergedResult ++ Seq(SearchResult(TopUsers, TopUsersSection, 0))
      }
    }

    def addContacts(): Unit = {
      if (contacts.nonEmpty || localResults.nonEmpty) {
        mergedResult = mergedResult ++ Seq(SearchResult(SectionHeader, ContactsSection, 0, teamName))
        var contactsSection = Seq[SearchResult]()

        contactsSection = contactsSection ++ contacts.indices.map { i =>
          val name = Option(contacts(i).getDetails).map(_.getDisplayName).getOrElse("")
          SearchResult(AddressBookContact, ContactsSection, i, name)
        }

        contactsSection = contactsSection ++ localResults.indices.map { i =>
          SearchResult(ConnectedUser, ContactsSection, i, localResults(i).id.str.hashCode, localResults(i).getDisplayName)
        }

        val shouldCollapse = searchUserController.searchState.currentValue.exists(_.filter.nonEmpty) && collapsedContacts && contactsSection.size > CollapsedContacts

        contactsSection = contactsSection.sortBy(_.name).take(if (shouldCollapse) CollapsedContacts else contactsSection.size)

        mergedResult = mergedResult ++ contactsSection
        if (shouldCollapse) {
          mergedResult = mergedResult ++ Seq(SearchResult(Expand, ContactsSection, 0))
        }
      }
    }

    def addGroupConversations(): Unit = {
      if (conversations.nonEmpty) {
        mergedResult = mergedResult ++ Seq(SearchResult(SectionHeader, GroupConversationsSection, 0, teamName))

        val shouldCollapse = collapsedGroups && conversations.size > CollapsedGroups

        mergedResult = mergedResult ++ conversations.indices.map { i =>
          SearchResult(GroupConversation, GroupConversationsSection, i, conversations(i).id.str.hashCode)
        }.take(if (shouldCollapse) CollapsedGroups else conversations.size)
        if (shouldCollapse) {
          mergedResult = mergedResult ++ Seq(SearchResult(Expand, GroupConversationsSection, 0))
        }
      }
    }

    def addConnections(): Unit = {
      if (directoryResults.nonEmpty) {
        mergedResult = mergedResult ++ Seq(SearchResult(SectionHeader, DirectorySection, 0))
        mergedResult = mergedResult ++ directoryResults.indices.map { i =>
          SearchResult(UnconnectedUser, DirectorySection, i, directoryResults(i).id.str.hashCode)
        }
      }
    }

    if (userAccountsController.isTeamAccount) {
      addContacts()
      addGroupConversations()
    } else {
      addTopPeople()
      addContacts()
      addGroupConversations()
    }
    addConnections()

    ZLog.debug(s"Merged contacts updated: ${mergedResult.size}")
    notifyDataSetChanged()
  }

  override def getItemCount = mergedResult.size

  override def onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = {
    val item = mergedResult(position)
    item.itemType match {
      case TopUsers =>
        holder.asInstanceOf[TopUsersViewHolder].bind(topUsers)
        holder.asInstanceOf[TopUsersViewHolder].bindOnItemTouchListener(topUsersOnItemTouchListener)
      case GroupConversation =>
        val conversation = conversations(item.index)
        holder.asInstanceOf[ConversationViewHolder].bind(conversation)
      case ConnectedUser =>
        val connectedUser = localResults(item.index)
        val contactIsSelected = searchUserController.selectedUsers.contains(connectedUser.id)
        holder.asInstanceOf[UserViewHolder].bind(connectedUser, contactIsSelected)
      case UnconnectedUser =>
        val unconnectedUser = directoryResults(item.index)
        val contactIsSelected = searchUserController.selectedUsers.contains(unconnectedUser.id)
        holder.asInstanceOf[UserViewHolder].bind(unconnectedUser, contactIsSelected)
      case SectionHeader =>
        holder.asInstanceOf[SectionHeaderViewHolder].bind(item.section, item.name)
      case NameInitialSeparator =>
        holder.asInstanceOf[AddressBookSectionHeaderViewHolder].bind(item.name)
      case AddressBookContact =>
        val contact  = contacts(item.index)
        holder.asInstanceOf[AddressBookContactViewHolder].bind(contact, contactsCallback)
      case Expand =>
        val itemCount = if (item.section == ContactsSection) contacts.size + localResults.size else conversations.size
        holder.asInstanceOf[SectionExpanderViewHolder].bind(itemCount, new View.OnClickListener() {
          def onClick(v: View): Unit = {
            if (item.section == ContactsSection) expandContacts() else expandGroups()
          }
        })
      case _ =>
    }
  }

  override def onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = {
    viewType match {
      case TopUsers =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.startui_top_users, parent, false)
        val topUserAdapter: TopUserAdapter = new TopUserAdapter(searchUserController.selectedUsersSignal)
        new com.waz.zclient.usersearch.viewholders.TopUsersViewHolder(view, topUserAdapter, parent.getContext)
      case ConnectedUser | UnconnectedUser =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.startui_user, parent, false)
        new UserViewHolder(view, true, darkTheme)
      case GroupConversation =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.startui_conversation, parent, false)
        new ConversationViewHolder(view, darkTheme)
      case SectionHeader =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.startui_section_header, parent, false)
        new SectionHeaderViewHolder(view)
      case NameInitialSeparator =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.startui_section_header, parent, false)
        new AddressBookSectionHeaderViewHolder(view, darkTheme)
      case AddressBookContact =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.contactlist_user, parent, false)
        new AddressBookContactViewHolder(view, darkTheme)
      case Expand =>
        val view = LayoutInflater.from(parent.getContext).inflate(R.layout.startui_section_expander, parent, false)
        new SectionExpanderViewHolder(view)
    }
  }

  override def getItemViewType(position: Int) = mergedResult.lift(position).fold(-1)(_.itemType)

  override def getItemId(position: Int) = mergedResult.lift(position).fold(-1L)(_.id)

  def getSectionIndexForPosition(position: Int) = mergedResult.lift(position).fold(-1)(_.index)

  private def expandContacts() = {
    collapsedContacts = false
    updateMergedResults()
  }

  private def expandGroups() = {
    collapsedGroups = false
    updateMergedResults()
  }
}

object PickUsersAdapter {

  //Item Types
  val TopUsers: Int = 0
  val NameInitialSeparator: Int = 1
  val AddressBookContact: Int = 2
  val ConnectedUser: Int = 3
  val UnconnectedUser: Int = 4
  val GroupConversation: Int = 5
  val SectionHeader: Int = 6
  val Expand: Int = 7

  //Sections
  val TopUsersSection = 0
  val GroupConversationsSection = 1
  val ContactsSection = 2
  val DirectorySection = 3

  //Constants
  val CollapsedContacts = 5
  val CollapsedGroups = 5

  trait Callback {
    def getSelectedUsers: Set[UserId]
    def onContactListUserClicked(userId: UserId): Unit
    def onContactListContactClicked(contactDetails: ContactDetails): Unit
  }

}

case class SearchResult(itemType: Int, section: Int, index: Int, id: Long, name: String)

object SearchResult{
  def apply(itemType: Int, section: Int, index: Int, id: Long): SearchResult = new SearchResult(itemType, section, index, id, "")
  def apply(itemType: Int, section: Int, index: Int, name: String): SearchResult = new SearchResult(itemType, section, index, itemType + section + index, name)
  def apply(itemType: Int, section: Int, index: Int): SearchResult = SearchResult(itemType, section, index, "")
}
