/*
 * Copyright 2016 Igor Maznitsa.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.igormaznitsa.sciareto.ui.tree;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;
import com.igormaznitsa.meta.common.utils.Assertions;
import com.igormaznitsa.sciareto.Context;
import com.igormaznitsa.sciareto.ui.UiUtils;

public final class ExplorerTree extends JScrollPane {

  private static final long serialVersionUID = 3894835807758698784L;

  private final JTree projectTree;
  private final Context context;
  
  public ExplorerTree(@Nonnull final Context context) {
    super();
    this.projectTree = new JTree() {
      @Override
      public String getToolTipText(@Nonnull final MouseEvent evt) {
        if (getRowForLocation(evt.getX(), evt.getY()) == -1) {
          return null;
        }
        final TreePath curPath = getPathForLocation(evt.getX(), evt.getY());
        final Object lastElement = curPath.getLastPathComponent();
        if (lastElement instanceof NodeFileOrFolder) {
          final File file = ((NodeFileOrFolder) lastElement).getFile();
          return file == null ? null : file.getAbsolutePath();
        } else {
          return null;
        }
      }
    };
    this.context = context;
    this.projectTree.setDragEnabled(true);

    ToolTipManager.sharedInstance().registerComponent(this.projectTree);

    this.projectTree.setCellRenderer(new TreeCellRenderer());
    this.projectTree.setModel(new NodeProjectGroup("."));
    this.projectTree.setRootVisible(false);
    this.setViewportView(this.projectTree);

    this.projectTree.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(@Nonnull final MouseEvent e) {
        if (e.getClickCount() > 1) {
          final int selRow = projectTree.getRowForLocation(e.getX(), e.getY());
          final TreePath selPath = projectTree.getPathForLocation(e.getX(), e.getY());
          if (selRow >= 0) {
            final NodeFileOrFolder node = (NodeFileOrFolder) selPath.getLastPathComponent();
            if (node != null) {
              final File file = node.getFile();
              if (!context.openFileAsTab(file)) {
                UiUtils.openInSystemViewer(file);
              }
            }
          }
        }
      }

      @Override
      public void mouseReleased(@Nonnull final MouseEvent e) {
        if (e.isPopupTrigger()) {
          final TreePath selPath = projectTree.getPathForLocation(e.getX(), e.getY());
          if (selPath != null) {
            final Object last = selPath.getLastPathComponent();
            if (last instanceof NodeFileOrFolder) {
              final JPopupMenu popupMenu = makePopupMenu((NodeFileOrFolder)last);
              if (popupMenu!=null){
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
              }
            }
          }
        }
      }

      @Override
      public void mousePressed(@Nonnull final MouseEvent e) {
        if (e.isPopupTrigger()) {
          final TreePath selPath = projectTree.getPathForLocation(e.getX(), e.getY());
          if (selPath != null) {
            final Object last = selPath.getLastPathComponent();
            if (last instanceof NodeFileOrFolder) {
              final JPopupMenu popupMenu = makePopupMenu((NodeFileOrFolder) last);
              if (popupMenu != null) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
              }
            }
          }
        }
      }

    });
  }

  public void сloseProject(@Nonnull final NodeProject tree){
    ((NodeProjectGroup)this.projectTree.getModel()).removeProject(tree);
    this.context.onCloseProject(tree);
  }

  public void focusToFileItem(@Nonnull final File file) {
    final NodeProjectGroup group = getCurrentGroup();
    final TreePath pathToFile = group.findPathToFile(file);
    if (pathToFile!=null){
      this.projectTree.setSelectionPath(pathToFile);
    }
  }
  
  @Nullable
  private JPopupMenu makePopupMenu(@Nonnull final NodeFileOrFolder node) {
    final JPopupMenu result = new JPopupMenu();
    if (node instanceof NodeProject){
      final JMenuItem close = new JMenuItem("Close");
      close.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(@Nonnull final ActionEvent e) {
          сloseProject((NodeProject)node);
        }
      });
      result.add(close);
      final JMenuItem refresh = new JMenuItem("Reload");
      refresh.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(@Nonnull final ActionEvent e) {
          getCurrentGroup().refreshProjectFolder((NodeProject)node);
        }
      });
      result.add(refresh);
    }
    
    if (!node.isLeaf()){
      final JMenuItem item  = new JMenuItem("New mind map");
      item.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(@Nonnull final ActionEvent e) {
          final File newfile = context.createMindMapFile(node.getFile());
          if (newfile!=null){
            getCurrentGroup().refreshProjectFolder(context.findProjectForFile(node.getFile()));
            context.openFileAsTab(newfile);
            context.focusInTree(newfile);
          }
        }
      });
      result.add(item);
    }
    
    return result;
  }

  @Nonnull
  public NodeProjectGroup getCurrentGroup() {
    return (NodeProjectGroup) this.projectTree.getModel();
  }

  public void setModel(@Nonnull final NodeProjectGroup model, final boolean expandFirst) {
    this.projectTree.setModel(Assertions.assertNotNull(model));
    if (expandFirst && model.getChildCount() > 0) {
      this.projectTree.expandPath(new TreePath(new Object[]{model, model.getChildAt(0)}));
    }
  }

}
