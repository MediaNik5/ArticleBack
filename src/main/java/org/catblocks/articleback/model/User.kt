package org.catblocks.articleback.model

import org.catblocks.articleback.security.Provider
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "users")
@Entity
class User (
    @Id
    var id: String,
    var username: String,
    var email: String,
    var imageUrl: String,
    var provider: Provider,
)