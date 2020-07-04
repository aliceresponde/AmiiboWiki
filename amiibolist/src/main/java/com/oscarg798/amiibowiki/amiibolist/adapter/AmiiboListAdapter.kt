/*
 * Copyright 2020 Oscar David Gallon Rosero
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *
 */

package com.oscarg798.amiibowiki.amiibolist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.oscarg798.amiibowiki.amiibolist.R
import com.oscarg798.amiibowiki.amiibolist.ViewAmiibo


class AmiiboListAdapter(private val amiiboClickListener: AmiiboClickListener) :
    ListAdapter<ViewAmiibo, AmiiboListViewHolder>(object :
        DiffUtil.ItemCallback<ViewAmiibo>() {
        override fun areItemsTheSame(oldItem: ViewAmiibo, newItem: ViewAmiibo): Boolean =
            oldItem.tail == newItem.tail

        override fun areContentsTheSame(oldItem: ViewAmiibo, newItem: ViewAmiibo): Boolean =
            oldItem == newItem

    }) {

    override fun onBindViewHolder(holder: AmiiboListViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            amiiboClickListener.onClick(getItem(holder.adapterPosition))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AmiiboListViewHolder {
        return AmiiboListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.amiibo_list_item, parent, false)
        )
    }
}