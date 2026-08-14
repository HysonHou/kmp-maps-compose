package eu.buney.maps.utils.clustering.algo

import eu.buney.maps.LatLng
import eu.buney.maps.utils.clustering.Cluster
import eu.buney.maps.utils.clustering.ClusterItem

/**
 * A cluster whose center is determined upon creation.
 */
internal class StaticCluster<T : ClusterItem>(
    override val position: LatLng,
) : Cluster<T> {
    private val _items: MutableCollection<T> = linkedSetOf()

    fun add(item: T): Boolean = _items.add(item)

    fun remove(item: T): Boolean = _items.remove(item)

    override val items: Collection<T> get() = _items

    override val size: Int get() = _items.size

    // 聚类在构建过程中会持续增删条目，不能让可变集合参与 HashSet 的哈希计算。
    // 否则对象加入 HashSet 后哈希值会变化，Kotlin/Native 在扩容重哈希时会抛出异常。
    override fun hashCode(): Int = position.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is StaticCluster<*>) return false
        return other.position == position && other._items == _items
    }
}
