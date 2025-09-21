package vstu.oop.model.core.world;

import vstu.oop.model.entity.mob.damage.Projectile;

import java.util.*;
import java.util.stream.Collectors;

public class ProjectileContainer implements Iterable<Projectile> {

    private final Collection<Projectile> projectiles;
    private boolean processingIsStoped = false;

    public ProjectileContainer() {
        projectiles = new ArrayList<>();
    }

    public void update(long currentTick) {
        if (processingIsStoped) {
            return;
        }

        projectiles.forEach(p -> p.act(currentTick));

        Set<Projectile> inactiveProjectiles = projectiles.stream()
                .filter(Projectile::isInactive)
                .collect(Collectors.toSet());

        projectiles.removeAll(inactiveProjectiles);
    }

    public boolean add(Projectile projectile) {
        if (processingIsStoped) {
            return false;
        }
        return projectiles.add(projectile);
    }

    public boolean addAll(Collection<Projectile> projectiles) {
        if (processingIsStoped) {
            return false;
        }
        return this.projectiles.addAll(projectiles);
    }

    @Override
    public Iterator<Projectile> iterator() {
        return Collections.unmodifiableCollection(projectiles).iterator();
    }

    public int size() {
        return projectiles.size();
    }

    void stopProcessingAndClear() {
        processingIsStoped = true;
        projectiles.clear();
    }

    public List<Projectile> getProjectilesSnapshot() {
        return new ArrayList<>(projectiles);
    }
}
