import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBasketItem } from '../basket-item.model';
import { BasketItemService } from '../service/basket-item.service';

@Component({
  templateUrl: './basket-item-delete-dialog.component.html',
})
export class BasketItemDeleteDialogComponent {
  basketItem?: IBasketItem;

  constructor(protected basketItemService: BasketItemService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.basketItemService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
