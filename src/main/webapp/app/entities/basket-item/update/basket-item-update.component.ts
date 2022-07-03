import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBasketItem, BasketItem } from '../basket-item.model';
import { BasketItemService } from '../service/basket-item.service';
import { IBasket } from 'app/entities/basket/basket.model';
import { BasketService } from 'app/entities/basket/service/basket.service';
import { IProduct } from 'app/entities/product/product.model';
import { ProductService } from 'app/entities/product/service/product.service';

@Component({
  selector: 'jhi-basket-item-update',
  templateUrl: './basket-item-update.component.html',
})
export class BasketItemUpdateComponent implements OnInit {
  isSaving = false;

  basketsSharedCollection: IBasket[] = [];
  productsSharedCollection: IProduct[] = [];

  editForm = this.fb.group({
    id: [],
    quantity: [null, [Validators.required]],
    totalCost: [null, [Validators.required]],
    basket: [],
    product: [],
  });

  constructor(
    protected basketItemService: BasketItemService,
    protected basketService: BasketService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ basketItem }) => {
      this.updateForm(basketItem);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const basketItem = this.createFromForm();
    if (basketItem.id !== undefined) {
      this.subscribeToSaveResponse(this.basketItemService.update(basketItem));
    } else {
      this.subscribeToSaveResponse(this.basketItemService.create(basketItem));
    }
  }

  trackBasketById(_index: number, item: IBasket): number {
    return item.id!;
  }

  trackProductById(_index: number, item: IProduct): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBasketItem>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(basketItem: IBasketItem): void {
    this.editForm.patchValue({
      id: basketItem.id,
      quantity: basketItem.quantity,
      totalCost: basketItem.totalCost,
      basket: basketItem.basket,
      product: basketItem.product,
    });

    this.basketsSharedCollection = this.basketService.addBasketToCollectionIfMissing(this.basketsSharedCollection, basketItem.basket);
    this.productsSharedCollection = this.productService.addProductToCollectionIfMissing(this.productsSharedCollection, basketItem.product);
  }

  protected loadRelationshipsOptions(): void {
    this.basketService
      .query()
      .pipe(map((res: HttpResponse<IBasket[]>) => res.body ?? []))
      .pipe(map((baskets: IBasket[]) => this.basketService.addBasketToCollectionIfMissing(baskets, this.editForm.get('basket')!.value)))
      .subscribe((baskets: IBasket[]) => (this.basketsSharedCollection = baskets));

    this.productService
      .query()
      .pipe(map((res: HttpResponse<IProduct[]>) => res.body ?? []))
      .pipe(
        map((products: IProduct[]) => this.productService.addProductToCollectionIfMissing(products, this.editForm.get('product')!.value))
      )
      .subscribe((products: IProduct[]) => (this.productsSharedCollection = products));
  }

  protected createFromForm(): IBasketItem {
    return {
      ...new BasketItem(),
      id: this.editForm.get(['id'])!.value,
      quantity: this.editForm.get(['quantity'])!.value,
      totalCost: this.editForm.get(['totalCost'])!.value,
      basket: this.editForm.get(['basket'])!.value,
      product: this.editForm.get(['product'])!.value,
    };
  }
}
