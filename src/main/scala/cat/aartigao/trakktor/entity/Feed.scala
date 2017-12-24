package cat.aartigao.trakktor.entity

import java.time.LocalDateTime

case class FeedItem(title: String,
                    description: String,
                    link: String,
                    author: String,
                    category: String,
                    pubDate: LocalDateTime)