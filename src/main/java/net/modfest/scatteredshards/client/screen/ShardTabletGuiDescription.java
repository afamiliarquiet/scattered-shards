package net.modfest.scatteredshards.client.screen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.WPanelWithInsets;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WScrollBar;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.modfest.scatteredshards.api.ShardCollection;
import net.modfest.scatteredshards.api.ShardLibrary;
import net.modfest.scatteredshards.api.shard.Shard;
import net.modfest.scatteredshards.client.ScatteredShardsClient;
import net.modfest.scatteredshards.client.screen.widget.WLeftRightPanel;
import net.modfest.scatteredshards.client.screen.widget.WShardPanel;
import net.modfest.scatteredshards.client.screen.widget.WShardSetPanel;
import net.modfest.scatteredshards.client.screen.widget.scalable.WScaledLabel;
import net.modfest.scatteredshards.networking.C2SRequestGlobalCollection;

public class ShardTabletGuiDescription extends LightweightGuiDescription {
	public static final int ROWS_PER_SCREEN = 5;

	protected final ShardCollection collection;
	protected final ShardLibrary library;

	WShardPanel shardPanel = new WShardPanel();
	WPlainPanel selectorPanel = new WPlainPanel();
	WScrollBar shardSelectorScrollBar = new WScrollBar(Axis.VERTICAL);
	WListPanel<Identifier, WShardSetPanel> shardSelector;

	public ShardTabletGuiDescription(ShardCollection collection, ShardLibrary library) {
		this.collection = collection;
		this.library = library;

		shardPanel.setShard(Shard.MISSING_SHARD);
		shardPanel.setHidden(true);

		List<Identifier> ids = new ArrayList<>(this.library.shardSets().keySet());
		ids.sort(Comparator.comparing(Identifier::getNamespace));

		shardSelector = new WListPanel<>(ids, WShardSetPanel::new, this::configurePanel);
		selectorPanel.setInsets(Insets.ROOT_PANEL);

		WLeftRightPanel root = new WLeftRightPanel(selectorPanel, shardPanel);
		selectorPanel.add(shardSelector, 0, 0, getLayoutWidth(selectorPanel), getLayoutHeight(selectorPanel));

		int panelHeight = selectorPanel.getHeight();

		WScaledLabel progressVisited = new WScaledLabel(() -> {
			if (!ScatteredShardsClient.hasShiftDown()) return Text.empty();
			int visitedSets = 0;
			for (Collection<Identifier> set : library.shardSets().asMap().values()) {
                for (Identifier identifier : set) {
                    if (collection.contains(identifier)) {
                        visitedSets++;
                        break;
                    }
                }
            }
			return Text.translatable("gui.scattered_shards.tablet.label.progress.started", "%.0f%%".formatted(100 * visitedSets / (float) library.shardSets().asMap().keySet().size()));
		}, 1.0f).setColor(Colors.LIGHT_GRAY);
		selectorPanel.add(progressVisited, 0, 0);
		progressVisited.setSize(80, 10);
		progressVisited.setLocation(13, panelHeight - 20);

		WScaledLabel progressTotal = new WScaledLabel(() -> {
			if (!ScatteredShardsClient.hasShiftDown()) return Text.empty();
			return Text.translatable("gui.scattered_shards.tablet.label.progress.total", "%.0f%%".formatted(100 * collection.size() / (float) library.shards().size()));
		}, 1.0f).setColor(Colors.LIGHT_GRAY);
		selectorPanel.add(progressTotal, 0, 0);
		progressTotal.setSize(80, 10);
		progressTotal.setLocation(selectorPanel.getWidth() - 72, panelHeight - 20);

		ClientPlayNetworking.send(C2SRequestGlobalCollection.INSTANCE);

		this.setRootPanel(root);

		root.validate(this);
	}

	private int getLayoutWidth(WPanelWithInsets panel) {
		return panel.getWidth() - panel.getInsets().left() - panel.getInsets().right();
	}

	private int getLayoutHeight(WPanelWithInsets panel) {
		return panel.getHeight() - panel.getInsets().top() - panel.getInsets().bottom();
	}

	private void configurePanel(Identifier setId, WShardSetPanel panel) {
		panel.setSize(shardSelector.getWidth() - shardSelector.getScrollBar().getWidth(), 20);
		panel.setShardConsumer(shardPanel::setShard);
		panel.setShardSet(setId, library, collection);
	}

	@Override
	public void addPainters() {
		selectorPanel.setBackgroundPainter(BackgroundPainter.createColorful(ScatteredShardsClient.LEFT));
	}

	public static class Screen extends CottonClientScreen {
		public Screen(ShardCollection collection, ShardLibrary library) {
			super(new ShardTabletGuiDescription(collection, library));
		}
	}
}
