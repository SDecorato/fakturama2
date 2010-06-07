/*
 * 
 *	Fakturama - Free Invoicing Software 
 *  Copyright (C) 2010  Gerd Bartelt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 */

package com.sebulli.fakturama.exportsales;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class ExportSalesWizard extends Wizard implements IExportWizard {
	ExportSalesWizandPage1 page1;

	public ExportSalesWizard() {
		setWindowTitle("Export");
		page1 = new ExportSalesWizandPage1();
		addPage(page1);
	}

	@Override
	public boolean performFinish() {
		SalesExporter salesExporter = new SalesExporter(page1.getBeginDate(), page1.getEndDate());
		return salesExporter.export();

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

}
