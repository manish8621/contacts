package com.mk.contacts.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mk.contacts.databinding.GroupListItemLayoutBinding
import com.mk.contacts.databinding.MultipleOptionItemLayoutBinding
import com.mk.contacts.model.room.Group


const val GROUP_OPTION_VIEW_TYPE = 0
const val GROUP_ITEM_VIEW_TYPE = 1


class GroupAdapter(private val viewType: Int):ListAdapter<Group,RecyclerView.ViewHolder>(GroupDiffUtilItemCallback()) {
    var clickListeners:ClickListeners? = null

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    //to use when groups need to be displayed short like options
    class GroupOptionViewHolder(private val binding: MultipleOptionItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(group: Group, clickListeners: ClickListeners?){
            binding.group = group
            binding.groupNameTv.setOnClickListener{
                clickListeners?.let {
                    it.onGroupOptionItemClickListener(group,binding)
                }
            }
        }
        companion object{
            fun from(parent:ViewGroup):GroupOptionViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MultipleOptionItemLayoutBinding.inflate(layoutInflater,parent,false)
                return GroupOptionViewHolder(binding)
            }
        }
    }
    //to use when groups are main elements like list
    class GroupItemViewHolder(private val binding: GroupListItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(group: Group, clickListeners: ClickListeners?){
            binding.group = group
            binding.root.setOnClickListener{
                clickListeners?.let {
                    it.onGroupItemClickListener(group,binding)
                }
            }
        }
        companion object{
            fun from(parent:ViewGroup):GroupItemViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GroupListItemLayoutBinding.inflate(layoutInflater,parent,false)
                return GroupItemViewHolder(binding)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            GROUP_OPTION_VIEW_TYPE-> GroupOptionViewHolder.from(parent)
            GROUP_ITEM_VIEW_TYPE-> GroupItemViewHolder.from(parent)
            else -> throw Exception("Illegal view type in group adapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(viewType){
            GROUP_OPTION_VIEW_TYPE -> (holder as GroupOptionViewHolder).bind(getItem(position),clickListeners)
            GROUP_ITEM_VIEW_TYPE -> (holder as GroupItemViewHolder).bind(getItem(position),clickListeners)
        }
    }
    fun setOnClickListener(clickListeners :ClickListeners){
        this.clickListeners = clickListeners
    }
    interface ClickListeners{
        fun onGroupOptionItemClickListener(group: Group,binding: MultipleOptionItemLayoutBinding)
        fun onGroupItemClickListener(group: Group,binding: GroupListItemLayoutBinding)
    }
}
class GroupDiffUtilItemCallback: DiffUtil.ItemCallback<Group>() {
    override fun areItemsTheSame(oldItem: Group, newItem: Group) = oldItem==newItem
    override fun areContentsTheSame(oldItem: Group, newItem: Group)= oldItem==newItem
}