import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BasketItemComponent } from './list/basket-item.component';
import { BasketItemDetailComponent } from './detail/basket-item-detail.component';
import { BasketItemUpdateComponent } from './update/basket-item-update.component';
import { BasketItemDeleteDialogComponent } from './delete/basket-item-delete-dialog.component';
import { BasketItemRoutingModule } from './route/basket-item-routing.module';

@NgModule({
  imports: [SharedModule, BasketItemRoutingModule],
  declarations: [BasketItemComponent, BasketItemDetailComponent, BasketItemUpdateComponent, BasketItemDeleteDialogComponent],
  entryComponents: [BasketItemDeleteDialogComponent],
})
export class BasketItemModule {}
