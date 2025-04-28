package com.nextiva.nextivaapp.android.features.rooms.view.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

enum class MessageBubbleType {
    NONE {
        override fun shape(direction: MessageBubbleDirection) =
            RoundedCornerShape(
                medium,
                medium,
                medium,
                medium
            )
    },

    TOP {
        override fun shape(direction: MessageBubbleDirection) =
            RoundedCornerShape(
                medium,
                medium,
                if (direction == MessageBubbleDirection.SENT) small else medium,
                if (direction == MessageBubbleDirection.SENT) medium else small
            )
    },

    MIDDLE {
        override fun shape(direction: MessageBubbleDirection) =
            RoundedCornerShape(
                if (direction == MessageBubbleDirection.SENT) medium else small,
                if (direction == MessageBubbleDirection.SENT) small else medium,
                if (direction == MessageBubbleDirection.SENT) small else medium,
                if (direction == MessageBubbleDirection.SENT) medium else small
            )
    },

    BOTTOM {
        override fun shape(direction: MessageBubbleDirection) =
            RoundedCornerShape(
                if (direction == MessageBubbleDirection.SENT) medium else small,
                if (direction == MessageBubbleDirection.SENT) small else medium,
                if (direction == MessageBubbleDirection.SENT) medium else medium,
                if (direction == MessageBubbleDirection.SENT) medium else medium
            )
    };

    val medium = 8.dp
    val small = 2.dp

    abstract fun shape(direction: MessageBubbleDirection): RoundedCornerShape
}

enum class MessageBubbleDirection {
    SENT, RECEIVED
}
