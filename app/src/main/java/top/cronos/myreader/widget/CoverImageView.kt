/*
 * This file is part of QYReader.
 * QYReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QYReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QYReader.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2020 - 2022 fengyuecanzhu
 */

package top.cronos.myreader.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import top.cronos.myreader.R
import top.cronos.myreader.util.utils.ImageLoader


class CoverImageView : androidx.appcompat.widget.AppCompatImageView {
    internal var width: Float = 0.toFloat()
    internal var height: Float = 0.toFloat()
    private var nameHeight = 0f
    private var authorHeight = 0f
    private val namePaint = TextPaint()
    private val authorPaint = TextPaint()
    private var name: String? = null
    private var author: String? = null
    private var loadFailed = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    init {
        namePaint.typeface = Typeface.DEFAULT_BOLD
        namePaint.isAntiAlias = true
        namePaint.textAlign = Paint.Align.CENTER
        namePaint.textSkewX = -0.2f
        authorPaint.typeface = Typeface.DEFAULT
        authorPaint.isAntiAlias = true
        authorPaint.textAlign = Paint.Align.CENTER
        authorPaint.textSkewX = -0.1f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = MeasureSpec.getSize(widthMeasureSpec)
        val measuredHeight = measuredWidth * 7 / 5
        super.onMeasure(
                widthMeasureSpec,
                MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
        namePaint.textSize = width / 6
        namePaint.strokeWidth = namePaint.textSize / 10
        authorPaint.textSize = width / 9
        authorPaint.strokeWidth = authorPaint.textSize / 10
        nameHeight = height / 2
        authorHeight = nameHeight + authorPaint.fontSpacing
    }

    override fun onDraw(canvas: Canvas) {
        if (width >= 10 && height > 10) {
            @SuppressLint("DrawAllocation")
            val path = Path()
            //四个圆角
            path.moveTo(10f, 0f)
            path.lineTo(width - 10, 0f)
            path.quadTo(width, 0f, width, 10f)
            path.lineTo(width, height - 10)
            path.quadTo(width, height, width - 10, height)
            path.lineTo(10f, height)
            path.quadTo(0f, height, 0f, height - 10)
            path.lineTo(0f, 10f)
            path.quadTo(0f, 0f, 10f, 0f)

            canvas.clipPath(path)
        }
        super.onDraw(canvas)
        if (!loadFailed) return
        name?.let {
            namePaint.color = Color.WHITE
            namePaint.style = Paint.Style.STROKE
            canvas.drawText(it, width / 2, nameHeight, namePaint)
            namePaint.color = Color.RED
            namePaint.style = Paint.Style.FILL
            canvas.drawText(it, width / 2, nameHeight, namePaint)
        }
        author?.let {
            authorPaint.color = Color.WHITE
            authorPaint.style = Paint.Style.STROKE
            canvas.drawText(it, width / 2, authorHeight, authorPaint)
            authorPaint.color = Color.RED
            authorPaint.style = Paint.Style.FILL
            canvas.drawText(it, width / 2, authorHeight, authorPaint)
        }
    }

    fun setHeight(height: Int) {
        val width = height * 5 / 7
        minimumWidth = width
    }

    private fun setText(name: String?, author: String?) {
        this.name =
                when {
                    name == null -> null
                    name.length > 5 -> name.substring(0, 4) + "…"
                    else -> name
                }
        this.author =
                when {
                    author == null -> null
                    author.length > 8 -> author.substring(0, 7) + "…"
                    else -> author
                }
    }

    fun load(path: String?, name: String?, author: String?) {
        setText(name, author)
        val options = RequestOptions ()
                .placeholder(R.mipmap.default_cover)
                .error(R.mipmap.default_cover)
                .centerCrop()
        ImageLoader.load(context, path)//Glide自动识别http://和file://
                /*.placeholder(R.mipmap.default_cover)
                .error(R.mipmap.default_cover)
                .centerCrop()*/
                .apply(options)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                    ): Boolean {
                        loadFailed = true
                        return false
                    }

                    override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                    ): Boolean {
                        loadFailed = false
                        return false
                    }

                })
                .into(this)
    }
}
